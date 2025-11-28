package websocket.commands;

import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
