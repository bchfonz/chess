package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import service.GameService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();
    private final ConnectionManager connections = new ConnectionManager();
    private final SqlGameDAO gameDAO = new SqlGameDAO();
    private final SqlAuthDAO authDAO = new SqlAuthDAO();
    private final GameService gameServiceObj = new GameService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
//        ctx.send(gson.toJson("Connection established"));
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
//        ctx.send(gson.toJson("Message received"));
        try {
            if(ctx.message().contains("MAKE_MOVE")){
                MakeMoveCommand makeMoveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                ChessMove move = makeMoveCommand.getMove();
                int gameID = makeMoveCommand.getGameID();
                String authToken = makeMoveCommand.getAuthToken();
                String username = authDAO.getAuth(authToken).username();
                makeMove(gameID, move, username, ctx);
            }
            else if(ctx.message().contains("CONNECT")){
                ConnectCommand connectCommand = gson.fromJson(ctx.message(), ConnectCommand.class);
                String username = authDAO.getAuth(connectCommand.getAuthToken()).username();
                connect(username, ctx.session, connectCommand.getGameID(), ctx, connectCommand.isPlayer(), connectCommand.getTeam());
            }
            else if(ctx.message().contains("LEGAL_MOVE")){

            }
            else {
//            ServerMessage serverMessage = gson.fromJson(ctx.message(), ServerMessage.class);
                UserGameCommand userGameCommand = gson.fromJson(ctx.message(), UserGameCommand.class);
                String username = authDAO.getAuth(userGameCommand.getAuthToken()).username();
                switch (userGameCommand.getCommandType()) {
                    case LEAVE -> leave(userGameCommand.getGameID(), username, ctx);
                    case RESIGN -> resign(userGameCommand.getGameID(), username, ctx);
                }
            }
//            switch (serverMessage.getServerMessageType()){
//                case LOAD_GAME -> connect(serverMessage.getAuthToken(), ctx.session);
//            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String playerName, Session session, int gameID, WsMessageContext ctx, boolean isPlayer, String team) throws IOException {
        boolean connected = connections.userConnected(session);
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        if(connected){
            String redrawBoard = "Redrawing board:";
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, redrawBoard);
            ctx.send(notification.toString());
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            ctx.send(loadGameMessage.toString());
        }
        else {
            connections.add(session);
            NotificationMessage notification;
            String joinedMessage = String.format("%s joined the game", playerName);
            String observerMessage = String.format("%s is observing the game", playerName);
            //Checks for either observer or player
            if (isPlayer) {
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinedMessage);
                gameServiceObj.joinGame(playerName, team, gameID);
            } else {
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, observerMessage);
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            ctx.send(loadGameMessage.toString());
            connections.broadcast(session, notification);
        }
    }

    private void makeMove(Integer gameID, ChessMove move, String username, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        if(game.isGameOver()){
            String gameOver = "Game is already over. No new moves can be made.";
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, gameOver);
            ctx.send(gson.toJson(notification));
            return;
        }
        boolean isWhiteTurn = Objects.equals(game.getTeamTurn(), ChessGame.TeamColor.WHITE);
        boolean isPlayerWhite = Objects.equals(username, gameData.whiteUsername());
        if(isWhiteTurn != isPlayerWhite){
            String invalidTurn = "Not your turn!";
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, invalidTurn);
            ctx.send(gson.toJson(notification));
        }
        else {
            try {
                game.makeMove(move);
                connections.loadGame(game);
                char startCol = columnNumConverter(move.getStartPosition().getColumn());
                char endCol = columnNumConverter(move.getEndPosition().getColumn());
                String message = "Move made: " + startCol + move.getStartPosition().getRow() + " to " + endCol + move.getEndPosition().getRow();
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(ctx.session, notification);
                if (game.isInCheckmate(game.getTeamTurn())) {
                    String checkmate = "Game over. " + game.getTeamTurn() + " is in checkmate!";
                    NotificationMessage checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmate);
                    connections.broadcast(null, checkmateNotification);
                    game.setGameOver(true);
                }
                else if (game.isInCheck(game.getTeamTurn())) {
                    String check = game.getTeamTurn() + " is in check!";
                    NotificationMessage checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, check);
                    connections.broadcast(null, checkNotification);
                }
                else if (game.isInStalemate(game.getTeamTurn())) {
                    String stalemate = "Game over. Game ended in stalemate!";
                    NotificationMessage stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, stalemate);
                    connections.broadcast(null, stalemateNotification);
                    game.setGameOver(true);
                }
                GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(gameID, updatedGameData);
            } catch (InvalidMoveException e) {
                String errorMsg = "Invalid move: " + e.getMessage();
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMsg);
                ctx.send(gson.toJson(errorMessage));
            }
        }
//        Validate move
//        Update game to represent move
//        Send LOAD_GAME message to all connections
//        Send NOTIFICATION message to all other connections
//        If in check or checkmate send NOTIFICATION message to all clients
    }

    private void leave(Integer gameID, String username, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        GameData updatedGameData;
        if(Objects.equals(gameData.whiteUsername(), username)){
            updatedGameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        else if(Objects.equals(gameData.blackUsername(), username)){
            updatedGameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }
        else{
            updatedGameData = gameData;
        }
        gameDAO.updateGame(gameID, updatedGameData);
        String leaveMessage = String.format("%s left the game", username);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMessage);
        connections.broadcast(ctx.session, notification);
        connections.remove(ctx.session);
    };
    private void resign(Integer gameID, String username, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        game.setGameOver(true);
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(gameID, updatedGameData);
        String resignMessage = username + " has resigned from the game. The game is over.";
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMessage);
        connections.broadcast(null, notification);
//        Server marks game as over. No new moves can be made. Game is updates in the database
//        Notification is sent to all clients
    }

    private char columnNumConverter(int col){
        if(col == 1) {
            return 'a';
        }
        else if(col == 2) {
            return 'b';
        }
        else if(col == 3) {
            return 'c';
        }
        else if(col == 4) {
            return 'd';
        }
        else if(col == 5) {
            return 'e';
        }
        else if(col == 6) {
            return 'f';
        }
        else if(col == 7) {
            return 'g';
        }
        else if(col == 8){
            return 'h';
        }
        else{
            return 0;
        }
    }
}