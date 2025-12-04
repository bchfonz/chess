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
    public GameplayUI(String url) throws ResponseException {
        this.url = url;
        this.wsf = new WebSocketFacade(url);
    }

    public void observeGame(String authToken, int gameID) throws ResponseException {
        wsf.connect(UserGameCommand.CommandType.CONNECT, authToken, gameID, null, false);
        gameplay(authToken, gameID, false, null);
    }
//
    public void joinGame(String authToken, int gameID, String team) throws ResponseException {
        wsf.connect(ConnectCommand.CommandType.CONNECT, authToken, gameID, team, true);
        gameplay(authToken, gameID, true, team);
    }

    public void gameplay(String authToken, int gameID, boolean isPlayer, String team) throws ResponseException {
        boolean leave = false;

        while(!leave){
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");
            var input = inputs[0];
            switch (input) {
                case "help" -> {
                    if(isPlayer){
                        playerCommandList();
                    }
                    else{
                        observerCommandList();
                    }
                }
                case "redraw" -> {
                    if(isPlayer){
                        wsf.connect(ConnectCommand.CommandType.CONNECT, authToken, gameID, team, true);
                    }
                    else{
                        wsf.connect(UserGameCommand.CommandType.CONNECT, authToken, gameID, null, false);

                    }
                }
                case "leave" -> {
                    System.out.println("Leaving game...");
                    leave = true;
                    wsf.leave(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                }
                case "move" -> {
                    if(!isPlayer){
                    System.out.println("Invalid command for observers");
                    observerCommandList();
                    continue;
                    }
                    if(!validNumArgs(3, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        System.out.println("Remember to put a space between <START> and <END> positions");
                        continue;
                    }
                    String startPos = inputs[1];
                    String endPos = inputs[2];
                    char startPosLetter = startPos.charAt(0);
                    int startPosCol = letterToNumber(startPosLetter);
                    int startPosRow = Character.getNumericValue(startPos.charAt(1));
                    char endPosLetter = endPos.charAt(0);
                    int endPosCol = letterToNumber(endPosLetter);
                    int endPosRow = Character.getNumericValue(endPos.charAt(1));
                    if(startPosRow > 8 || startPosRow < 1 || startPosCol > 8 || startPosCol < 1 ||
                            endPosRow > 8 || endPosRow < 1 || endPosCol > 8 || endPosCol < 1){
                        System.out.println("Invalid position. Rows must be 1-8 and columns must be a-h");
                        continue;
                    }
                    ChessMove move = createMove(startPosRow, startPosCol, endPosRow, endPosCol);
                    wsf.makeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);

                }
                case "resign" -> {
                    if(!isPlayer){
                        System.out.println("Invalid command for observers");
                        observerCommandList();
                        continue;
                    }
                    System.out.println("Are you sure you want to resign? (yes/no)");
                    String response = scanner.nextLine();
                    if(Objects.equals(response, "yes")) {
                        wsf.leave(UserGameCommand.CommandType.RESIGN, authToken, gameID);
                    }
                }
                case "legal" -> {
                    String piecePos = inputs[1];
                    char piecePosLetter = piecePos.charAt(0);
                    int piecePosCol = letterToNumber(piecePosLetter);
                    int piecePosRow = Character.getNumericValue(piecePos.charAt(1));
                    if(piecePosRow > 8 || piecePosRow < 1 || piecePosCol > 8 || piecePosCol < 1) {
                        System.out.println("Invalid position. Rows must be 1-8 and columns must be a-h");
                        continue;
                    }
                    ChessPosition position = new ChessPosition(piecePosRow, piecePosCol);
                    wsf.getLegalMoves(position, team);
                }

                default ->{
                    System.out.println("Unknown command. Commands:");
                    if(isPlayer){
                        playerCommandList();
                    }
                    else{
                        observerCommandList();
                    }
                }
            }
        }
    }
    private void playerCommandList() {
        System.out.println("redraw - the board");
        System.out.println("leave - the game");
        System.out.println("move <START> <END> - make a move");
        System.out.println("resign - from the game");
        System.out.println("legal <PIECE POSITION> - highlights legal moves for a piece");
        System.out.println("help - with possible commands");
    }
    private void observerCommandList() {
        System.out.println("redraw - the board");
        System.out.println("leave - the game");
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
}
