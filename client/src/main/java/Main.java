import chess.*;
import exception.ResponseException;
import server.Server;
import service.LoginRequest;
import service.RegisterRequest;
import server.ServerFacade;
import ui.PostLoginUI;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    static ServerFacade facade;
    static PostLoginUI postLoginUI;
//    static Server server;
    public static void init(){
//        server = new Server();
//        var port = server.run(0);
        int port = 8080;
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
        postLoginUI = new PostLoginUI(facade);
    }
//    private static ServerFacade serverFacadeObj = new ServerFacade();
    public static void main(String[] args) {
        Main.init();
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
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
                case "register" -> {
                    String username = inputs[1];
                    String password= inputs[2];
                    String email = inputs[3];
                    RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                    try {
                        var response = facade.register(registerRequest);
                        if(response != null) {
                            if (response.authToken().length() > 10) {
                                System.out.println("Logged in as " + response.username());
                                exit = postLoginUI.postLogin();
                            }
                        }

                    } catch (ResponseException e) {
//                        System.out.println("In response excepti");
//                        System.out.println("Exception e: " + e);
                        throw new RuntimeException(e);
                    }
                }
                case "login" -> {
                    System.out.println("In Login");
                    String username = inputs[1];
                    String password= inputs[2];
                    LoginRequest loginRequest = new LoginRequest(username, password);
                    try {
                        var response = facade.login(loginRequest);
                        if(response != null) {
                            if (response.authToken().length() > 10) {
                                System.out.println("Logged in as " + response.username());
                                exit = postLoginUI.postLogin();
                            }
                        }

                    } catch (ResponseException e) {
//                        System.out.println("In response excepti");
//                        System.out.println("Exception e: " + e);
                        throw new RuntimeException(e);
                    }
                }
                case "quit" -> {
                    exit = true;
//                    server.stop();
                }
                case null, default -> {
                    System.out.println("Invalid input. Valid inputs:");
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
            }
        }
        System.out.println("Thanks for playing!");

    }
}