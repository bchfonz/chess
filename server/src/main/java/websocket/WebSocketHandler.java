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
import java.util.HashMap;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();
    public final HashMap<Integer, ConnectionManager> gameConnections = new HashMap<>();
//    private final ConnectionManager connections = new ConnectionManager();
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
                if (authDAO.getAuth(makeMoveCommand.getAuthToken()) == null) {
                    ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid auth token");
                    ctx.send(new Gson().toJson(error));
                    return;
                }
                String authToken = makeMoveCommand.getAuthToken();
                String username = authDAO.getAuth(authToken).username();
                makeMove(gameID, move, username, ctx);
            }
            else if(ctx.message().contains("CONNECT")){
                ConnectCommand connectCommand = gson.fromJson(ctx.message(), ConnectCommand.class);
                if (authDAO.getAuth(connectCommand.getAuthToken()) == null) {
                    ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid auth token");
                    ctx.send(new Gson().toJson(error));
                    return;
                }
                if(!gameConnections.containsKey(connectCommand.getGameID())) {
                    gameConnections.put(connectCommand.getGameID(), new ConnectionManager());
                }
                String username = authDAO.getAuth(connectCommand.getAuthToken()).username();
                connect(username, ctx.session, connectCommand.getGameID(), ctx, connectCommand.isPlayer(), connectCommand.getTeam());
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
        ConnectionManager connections = gameConnections.get(gameID);
        boolean connected = connections.userConnected(session);
        GameData gameData;
        ChessGame game = null;
        if(gameDAO.getGame(gameID) != null){
            gameData = gameDAO.getGame(gameID);
            if(gameData.game() != null){
                game = gameData.game();
            }
        }
        if(game == null){
            String message = "Game data not found.";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            ctx.send(errorMessage.toString());
            return;
        }
        if(connected){
            String message = "Redrawing board:";
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            ctx.send(notification.toString());
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            ctx.send(loadGameMessage.toString());
        }
        else {
            connections.add(session);
            NotificationMessage notification;
            //Checks for either observer or player
            if (isPlayer) {
                String message = String.format("%s joined the game", playerName);
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                gameServiceObj.joinGame(playerName, team, gameID);
            } else {
                String message = String.format("%s is observing the game", playerName);
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
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
            String message = "Game is already over. No new moves can be made.";
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            ctx.send(gson.toJson(error));
            return;
        }
        boolean isWhiteTurn = Objects.equals(game.getTeamTurn(), ChessGame.TeamColor.WHITE);
        boolean isPlayerWhite = Objects.equals(username, gameData.whiteUsername());
        if(isWhiteTurn != isPlayerWhite){
            String message = "Not your turn!";
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            ctx.send(gson.toJson(error));
        }
        else {
            ConnectionManager connections = gameConnections.get(gameID);
            try {
                game.makeMove(move);
                connections.loadGame(game);
                char startCol = columnNumConverter(move.getStartPosition().getColumn());
                char endCol = columnNumConverter(move.getEndPosition().getColumn());
                String message = "Move made: " + startCol + move.getStartPosition().getRow() + " to " + endCol + move.getEndPosition().getRow();
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(ctx.session, notification);
                if (game.isInCheckmate(game.getTeamTurn())) {
                    message = "Game over. " + game.getTeamTurn() + " is in checkmate!";
                    NotificationMessage checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcast(null, checkmateNotification);
                    game.setGameOver(true);
                }
                else if (game.isInCheck(game.getTeamTurn())) {
                    message = game.getTeamTurn() + " is in check!";
                    NotificationMessage checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcast(null, checkNotification);
                }
                else if (game.isInStalemate(game.getTeamTurn())) {
                    message = "Game over. Game ended in stalemate!";
                    NotificationMessage stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
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
        ConnectionManager connections = gameConnections.get(gameID);
        gameDAO.updateGame(gameID, updatedGameData);
        String message = String.format("%s left the game", username);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(ctx.session, notification);
        connections.remove(ctx.session);
    };
    private void resign(Integer gameID, String username, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        boolean isObserver = !Objects.equals(gameData.whiteUsername(), username) && !Objects.equals(gameData.blackUsername(), username);
        if(game.isGameOver()){
            String message = "Game is already over. No new moves can be made.";
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            ctx.send(gson.toJson(error));
            return;
        }
        if(isObserver){
            String message = "Observers cannot resign from the game.";
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            ctx.send(gson.toJson(error));
            return;
        }
        game.setGameOver(true);
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(gameID, updatedGameData);
        String message = username + " has resigned from the game. The game is over.";
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        ConnectionManager connections = gameConnections.get(gameID);
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