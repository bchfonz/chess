import chess.*;
import dataaccess.DatabaseManager;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");

        try {
            new DatabaseManager();
            System.out.println("Database and tables successfully set up!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}