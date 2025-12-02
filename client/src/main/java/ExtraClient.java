import exception.ResponseException;
import server.LoginRequest;
import server.RegisterRequest;
import server.ServerFacade;
import ui.PostLoginUI;

import java.util.Scanner;

public class ExtraClient {
    static ServerFacade facade;
    static PostLoginUI postLoginUI;
    //    static Server server;
    public static void init() throws ResponseException {
//        server = new Server();
//        var port = server.run(0);
        int port = 8080;
        System.out.println("Started test HTTP server on " + port);
        String url = String.format("http://localhost:%d", port);
        facade = new ServerFacade(url);
        postLoginUI = new PostLoginUI(facade, url);
    }
    //    private static ServerFacade serverFacadeObj = new ServerFacade();
    public static void main(String[] args) throws ResponseException {
        ExtraClient.init();
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        for (var i = 0; i < args.length; i++) {
            System.out.printf("%d. %s%n", i+1, args[i]);
        }
        System.out.println("♕ Welcome to 240 Chess. Type \"help\" to get started. ♕");
        boolean exit = false;
        while(!exit){
            System.out.print("[LOGGED_OUT] >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");
            var input = inputs[0];
            switch (input) {
                case "help" -> {
                    System.out.println("List of commands: (Commands are case sensitive)");
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
                case "register" -> {
                    if(!validNumArgs(4, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        continue;
                    }
                    String username = inputs[1];
                    String password = inputs[2];
                    String email = inputs[3];
                    RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                    var response = facade.register(registerRequest);
                    if (response == null) {
                        continue;
                    }
                    if (response.authToken().length() > 10) {
                        System.out.println("Logged in as " + response.username());
                        exit = postLoginUI.postLogin(response.authToken());
                    }


                }
                case "login" -> {
                    if(!validNumArgs(3, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        continue;
                    }
                    String username = inputs[1];
                    String password= inputs[2];
                    LoginRequest loginRequest = new LoginRequest(username, password);
                    var response = facade.login(loginRequest);
                    if(response == null) {
                        continue;
                    }
                    if (response.authToken().length() > 10) {
                        System.out.println("Logged in as " + response.username());
                        exit = postLoginUI.postLogin(response.authToken());
                    }
                }
                case "quit" -> {
                    exit = true;
                }
                case null, default -> {
                    System.out.println("Unknown command. Commands:");
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
            }
        }
        System.out.println("Thanks for playing!");
    }
    public static boolean validNumArgs(int expected, int actual){
        return expected == actual;
    }

}