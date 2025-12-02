package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage{
    ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
