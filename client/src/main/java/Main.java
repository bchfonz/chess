import chess.*;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
                    System.out.println("In register");
                }
                case "login" -> {
                    System.out.println("In Login");
                }
                case "quit" -> {
                    exit = true;
                    System.out.println("Thanks for playing!");
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

    }
}