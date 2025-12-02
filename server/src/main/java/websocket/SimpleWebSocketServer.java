package websocket;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWebSocketServer {

    // Keep track of all connected clients so we can broadcast
    private static final Set<WsContext> sessions =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void main(String[] args) {

        // Create and start Javalin on port 7070
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7070);

        // WebSocket endpoint at ws://localhost:7070/chat
        app.ws("/ws", ws -> {

            // When a client connects
            ws.onConnect(ctx -> {
                System.out.println("New client connected: ");
                sessions.add(ctx);
                ctx.send("Welcome! Your session id is ");
            });

            // When a message is received
            ws.onMessage(ctx -> {
                String msg = ctx.message();
                    // Ignore heartbeat messages
                System.out.println("This is the message:" + msg);
                ctx.send("Server echo: " + msg);


                // Echo back to the sender


                // Broadcast to all other clients

            });

            // When a client disconnects
            ws.onClose(ctx -> {
                sessions.remove(ctx);
                System.out.println("Client disconnected: ");
            });

            // On error
            ws.onError(ctx -> {
                System.err.println(
                        "WebSocket error from " + ": " + ctx.error()
                );
            });
        });

        System.out.println("WebSocket server running on ws://localhost:7070/ws");
    }
}
