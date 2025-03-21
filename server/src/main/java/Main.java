import chess.*;
import server.Server;
import dataaccess.*;
import service.*;
//TODO: import dataaccess classes, server classes, and service classes
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        // var piece = new ChessPiece(ChessGame.TeamColor.WHITE,
        // ChessPiece.PieceType.PAWN);
        // System.out.println("♕ 240 Chess Server: " + piece);
        Server testServer = new Server();
        int port = testServer.run(8000); // Choose a port (default: 4567)
        System.out.println("Server running on port: " + port);
        Spark.post("/test_endpoint", (req, res) -> {

            // String username = req.queryParams("username");
            // String password = req.queryParams("password");
            // System.out.println(username + " " + password);

            // Simulate user creation
            // In a real application, you would interact with your data access layer here
            return "{\"message\": \"User created!\"}";
        });

        // Practice creating endpoints here

    }

}

// import static spark.Spark.*;

// public class TestEndpoint {
// public static void registerRoutes() {
// get("/test_endpoint", (req, res) -> {
// res.type("application/json");
// return "{\"message\": \"Hello from TestEndpoint!\"}";
// });
// }
// }
// import static spark.Spark.*;

// public class Main {
// public static void main(String[] args) {
// port(8000); // Set the port

// // Register endpoints from TestEndpoint
// TestEndpoint.registerRoutes();

// // Other route groups can be added the same way
// }
// }