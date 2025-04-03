import chess.*;
import server.Server;
import dataaccess.*;
import service.*;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        // var piece = new ChessPiece(ChessGame.TeamColor.WHITE,
        // ChessPiece.PieceType.PAWN);
        // System.out.println("♕ 240 Chess Server: " + piece);
        Server testServer = new Server();
        int port = testServer.run(8000); // Choose a port (default: 4567)
        System.out.println("Server running on port: " + port);

    }

}
