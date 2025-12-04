package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(Session session) {
        connections.put(session, session);
        System.out.println("Added session. Total connections: " + connections.size());
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public boolean userConnected(Session session) {
        return connections.containsKey(session);
    }

    public void broadcast(Session excludeSession, NotificationMessage notification) throws IOException {
//        String msg = notification.getNotificationMessage();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(gson.toJson(notification));
                }
            }
        }
    }

    public void loadGame(ChessGame game){
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        for (Session c : connections.values()) {
            try {
                if (c.isOpen()) {
                    c.getRemote().sendString(loadGameMessage.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}