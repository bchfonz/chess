package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage{
    String notificationMessage;
    public NotificationMessage(ServerMessageType type, String notificationMessage) {
        super(type);
        this.notificationMessage = notificationMessage;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
