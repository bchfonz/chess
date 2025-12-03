package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
