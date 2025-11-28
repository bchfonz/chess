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
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();
    private final ConnectionManager connections = new ConnectionManager();
    private final SqlGameDAO gameDAO = new SqlGameDAO();
    private final SqlAuthDAO authDAO = new SqlAuthDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
        ctx.send(gson.toJson("Connection established"));
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        ctx.send(gson.toJson("Message received"));
        try {
            if(ctx.message().contains("MAKE_MOVE")){
                MakeMoveCommand makeMoveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                ChessMove move = makeMoveCommand.getMove();
                int gameID = makeMoveCommand.getGameID();
                makeMove(gameID, move, ctx);
            }
            else {
//            ServerMessage serverMessage = gson.fromJson(ctx.message(), ServerMessage.class);
                UserGameCommand userGameCommand = gson.fromJson(ctx.message(), UserGameCommand.class);
                String username = authDAO.getAuth(userGameCommand.getAuthToken()).username();
                switch (userGameCommand.getCommandType()) {
                    case CONNECT -> connect(username, ctx.session, userGameCommand.getGameID(), ctx);
                    case LEAVE -> leave();
                    case RESIGN -> resign();
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

    private void connect(String visitorName, Session session, int gameID, WsMessageContext ctx) throws IOException {
        connections.add(session);
        System.out.println("In connect in WebSocketHandler");
        String message = String.format("%s joined the game", visitorName);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        ctx.send(gson.toJson("Connected to game " + gameID));
        ChessGame game = gameDAO.getGame(gameID).game();
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        ctx.send(loadGameMessage.toString() );
        connections.broadcast(session, notification);
    }

    private void makeMove(Integer gameID, ChessMove move, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        try{
            game.makeMove(move);
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(gameID, updatedGame);
            connections.loadGame(game);
            String message = "Move made: " + move;
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(ctx.session, notification);
            if(game.isInCheckmate(game.getTeamTurn())){
                String checkmate = game.getTeamTurn() + " is in checkmate!";
                NotificationMessage checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmate);
                connections.broadcast(null, checkmateNotification);
            }
            if(game.isInCheck(game.getTeamTurn())){
                String check = game.getTeamTurn() + " is in check!";
                NotificationMessage checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, check);
                connections.broadcast(null, checkNotification);
            }
            if(game.isInStalemate(game.getTeamTurn())){
                String stalemate = "Game ended in stalemate!";
                NotificationMessage stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, stalemate);
                connections.broadcast(null, stalemateNotification);
            }
        } catch (InvalidMoveException e) {
            System.out.println("Invalid move exception message: " + e.getMessage());
            ctx.send(gson.toJson(e.getMessage()));
        }
//        Validate move
//        Update game to represent move
//        Send LOAD_GAME message to all connections
//        Send NOTIFICATION message to all other connections
//        If in check or checkmate send NOTIFICATION message to all clients
    }

    private void leave(){
//        Player is removed from game and game is updated
//        Notification is sent to all other clients
    };
    private void resign() throws IOException {
//        Server marks game as over. No new moves can be made. Game is updates in the database
//        Notification is sent to all clients
    }


    private void leave(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeMove(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}