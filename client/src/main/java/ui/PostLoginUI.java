package ui;

import server.ServerFacade;

public class PostLoginUI {
    ServerFacade facade;
    public PostLoginUI(ServerFacade facade){
        this.facade = facade;
    }

    public boolean postLogin(){
        boolean exit = false;
        boolean quit = false;
        while(!exit){
            System.out.print("[LOGGED_IN] >>> ");
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");
            var input = inputs[0];

            switch (input) {
                case "help" -> {
                    System.out.println("create <NAME> - a game");
                    System.out.println("list - games");
                    System.out.println("join <ID> [WHITE|BLACK] - a game");
                    System.out.println("observe <ID> - a game");
                    System.out.println("logout - when you are done");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
                case "create" -> {
                    String gameName = inputs[1];
                    System.out.println("Creating a game named " + gameName + "...");
                    // Additional logic to create a game can be added here
                }
                case "list" -> {

                }
                case "join" -> {

                }
                case "observe" -> {

                }
                case "logout" -> {
                    exit = true;
                    System.out.println("Logged out successfully.");
                }
                case "quit" -> {
                    exit = true;
                    quit = true;
                }
                case "view_games" -> {
                    System.out.println("Fetching your ongoing games...");
                    // Additional logic to fetch and display ongoing games can be added here
                }
                default -> System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
        return quit;
    }
}
