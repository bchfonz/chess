package ui;

import chess.*;
import exception.ResponseException;
import model.GameData;
import server.CreateGameRequest;
import server.JoinGameRequest;
import server.ListGamesResult;
import websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class GameplayUI {
    private final WebSocketFacade wsf;
    String url;
    boolean isPlayer = true;
    public GameplayUI(String url) throws ResponseException {
        this.url = url;
        this.wsf = new WebSocketFacade(url);
    }

    public void observeGame(String authToken, int gameID) throws ResponseException {
        System.out.println("In observeGame");
        isPlayer = false;
        wsf.connect(UserGameCommand.CommandType.CONNECT, authToken, gameID, null, false);
        gameplay(authToken, gameID);
    }
//
    public void joinGame(String authToken, int gameID, String team) throws ResponseException {
        System.out.println("In joinGame");
        isPlayer = true;
        wsf.connect(ConnectCommand.CommandType.CONNECT, authToken, gameID, team, true);
        gameplay(authToken, gameID);
    }

    public void gameplay(String authToken, int gameID) throws ResponseException {
        boolean leave = false;

        while(!leave){
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");
            var input = inputs[0];
            switch (input) {
                case "help" -> {
                    commandList();
                }
                case "redraw" -> {

                }
                case "leave" -> {
                    System.out.println("Leaving game...");
                    leave = true;

                }
                case "move" -> {
                    if(!validNumArgs(3, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        System.out.println("Remember to put a space between <START> and <END> positions");
                        continue;
                    }
                    String startPos = inputs[1];
                    String endPos = inputs[2];
                    char startPosLetter = startPos.charAt(0);
                    int startPosRow = letterToNumber(startPosLetter);
                    int startPosCol = Character.getNumericValue(startPos.charAt(1));
                    char endPosLetter = endPos.charAt(0);
                    int endPosRow = letterToNumber(endPosLetter);
                    int endPosCol = Character.getNumericValue(endPos.charAt(1));
                    if(startPosRow > 8 || startPosRow < 1 || startPosCol > 8 || startPosCol < 1 ||
                            endPosRow > 8 || endPosRow < 1 || endPosCol > 8 || endPosCol < 1){
                        System.out.println("Invalid position. Rows must be 1-8 and columns must be a-h l");
                        continue;
                    }
                    ChessMove move = createMove(startPosRow, startPosCol, endPosRow, endPosCol);
                    wsf.makeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);

                }
                case "resign" -> {

                }
                case "legal" -> {
                }

                default ->{
                    System.out.println("Unknown command. Commands:");
                    commandList();
                }
            }
        }
    }
    private void commandList() {
        System.out.println("redraw - the board");
        System.out.println("leave - the game");
        System.out.println("move <START> <END> - make a move");
        System.out.println("resign - from the game");
        System.out.println("legal <PIECE POSITION> - highlights legal moves for a piece");
        System.out.println("help - with possible commands");
    }

    private ChessMove createMove (int startPosRow, int startPosCol, int endPosRow, int endPosCol) {
        ChessPosition start = new ChessPosition(startPosRow, startPosCol);
        ChessPosition end = new ChessPosition(endPosRow, endPosCol);
        return new ChessMove(start, end, null);
    }

    private int letterToNumber(char letter) {
        int number = 0;
        if(letter == 'a'){
            number = 1;
        }
        else if(letter == 'b'){
            number = 2;
        }
        else if(letter == 'c'){
            number = 3;
        }
        else if(letter == 'd'){
            number = 4;
        }
        else if(letter == 'e'){
            number = 5;
        }
        else if(letter == 'f'){
            number = 6;
        }
        else if(letter == 'g'){
            number = 7;
        }
        else if(letter == 'h'){
            number = 8;
        }
        return number;
    }

    private boolean validNumArgs(int expected, int actual){
        return expected == actual;
    }

//    public void observeGame(){
//        ChessGame game = new ChessGame();
//        boardWhite(game);
//    }

//    public void boardBlack(ChessGame game){
//        ChessBoard board = game.getBoard();
//        System.out.print(EscapeSequences.ERASE_SCREEN);
//
//        // --- Print top border with file labels (a-h)
//        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        System.out.print("   "); // Left margin before A
//        for (char c = 'h'; c >= 'a'; c--) {
//            System.out.print(" " + c + " ");
//        }
//        System.out.print("   ");
//        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//        // --- Print board rows ---
//        for (int row = 1; row <= 8; row++) {
////            int borderNumber = 0 + row; // Row label (8 at top, 1 at bottom)
//
//            // Left border with rank label
//            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//            // Print board squares
//            for (int col = 8; col >= 1; col--) {
//                boolean isDarkSquare = (row + col) % 2 == 0;
//
//                String backgroundColor = isDarkSquare
//                        ? EscapeSequences.SET_BG_COLOR_DARK_GREY
//                        : EscapeSequences.SET_BG_COLOR_BLUE;
//                boolean isWhitePiece = true;
//                String pieceString = " ";
//                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                if(piece != null) {
//                    isWhitePiece = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
//                    pieceString = chessPieceString(piece);
//                }
//
//
//
//                String textColor = isWhitePiece
//                        ? EscapeSequences.SET_TEXT_COLOR_WHITE
//                        : EscapeSequences.SET_TEXT_COLOR_BLACK;
//
//                System.out.print(backgroundColor + textColor + " " + pieceString + " " +
//                        EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//            }
//
//            // Right border with rank label
//            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//            System.out.println();
//        }
//
//        // --- Bottom border with file labels again ---
//        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        System.out.print("   "); // Left margin
//        for (char c = 'h'; c >= 'a'; c--) {
//            System.out.print(" " + c + " ");
//        }
//        System.out.print("   ");
//        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//    }

//    public void boardWhite(ChessGame game){
//        ChessBoard board = game.getBoard();
//        System.out.print(EscapeSequences.ERASE_SCREEN);
//
//// --- Print top border with file labels (h–a for Black’s view)
//        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        System.out.print("   "); // Left margin before H
//        for (char c = 'a'; c <= 'h'; c++) {
//            System.out.print(" " + c + " ");
//        }
//        System.out.print("   ");
//        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//// --- Print board rows (from 1 at top to 8 at bottom)
//        for (int row = 8; row >= 1; row--) {
//            int borderNumber = 9 - row; // Will now show 1–8 from top to bottom
//
//            // Left border with rank label
//            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//            // Print board squares
//            for (int col = 1; col <= 8; col++) {
//                boolean isDarkSquare = (row + col) % 2 == 0;
//
//                String backgroundColor = isDarkSquare
//                        ? EscapeSequences.SET_BG_COLOR_DARK_GREY
//                        : EscapeSequences.SET_BG_COLOR_BLUE;
//                boolean isWhitePiece = false;
//                String pieceString = " ";
//                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                if (piece != null) {
//                    isWhitePiece = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
//                    pieceString = chessPieceString(piece);
//                }
//
//                String textColor = isWhitePiece
//                        ? EscapeSequences.SET_TEXT_COLOR_WHITE
//                        : EscapeSequences.SET_TEXT_COLOR_BLACK;
//
//                System.out.print(backgroundColor + textColor + " " + pieceString + " " +
//                        EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//            }
//
//            // Right border with rank label
//            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//            System.out.println();
//        }
//
//// --- Bottom border with file labels again (h–a)
//        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        System.out.print("   "); // Left margin
//        for (char c = 'a'; c <= 'h'; c++) {
//            System.out.print(" " + c + " ");
//        }
//        System.out.print("   ");
//        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
//
//    }

//    private String chessPieceString (ChessPiece piece){
//        if(piece.getPieceType() == ChessPiece.PieceType.KING){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♔";
//            }
//            else{
//                return "♚";
//            }
//        }
//        else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♕";
//            }
//            else{
//                return "♛";
//            }
//        }
//        else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♗";
//            }
//            else{
//                return "♝";
//            }
//        }
//        else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♘";
//            }
//            else{
//                return "♞";
//            }
//        }
//        else if(piece.getPieceType() == ChessPiece.PieceType.ROOK){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♖";
//            }
//            else{
//                return "♜";
//            }
//        }
//        else if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
//            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
//                return "♙";
//            }
//            else{
//                return "♟";
//            }
//        }
//        else{
//            return " ";
//        }
//    }
//        System.out.println("Game toString(): " + game.game().getBoard());
}
