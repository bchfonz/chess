package websocket;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SimpleWebSocketClient {

    public static void main(String[] args) throws Exception {
        String uri = "ws://localhost:8080/ws";

        HttpClient client = HttpClient.newHttpClient();

        CompletableFuture<Void> doneFuture = new CompletableFuture<>();

        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(uri), new Listener() {

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("Connected to server");

                        // Send a few messages
                        webSocket.sendText("Hello from Java client!", true);
                        webSocket.sendText("How are you, server?", true);
                        webSocket.sendText("How are you, server?", true);
                        webSocket.sendText("How are you, server?", true);
                        webSocket.sendText("How are you, server?", true);
                        webSocket.sendText("Hello from Java client!", true);

                        Listener.super.onOpen(webSocket);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                                                     CharSequence data,
                                                     boolean last) {
                        System.out.println("Received from server: " + data);
                        return Listener.super.onText(webSocket, data, last);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket,
                                                      int statusCode,
                                                      String reason) {
                        System.out.println("WebSocket closed: " + statusCode + " " + reason);
                        doneFuture.complete(null);
                        return Listener.super.onClose(webSocket, statusCode, reason);
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                        doneFuture.completeExceptionally(error);
                        Listener.super.onError(webSocket, error);
                    }
                }).join();

        // Give time to exchange messages
        Thread.sleep(2000);

        // Close the connection politely
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Bye").join();

        // Wait until closed
        doneFuture.join();
        System.out.println("Client finished.");
    }
}
