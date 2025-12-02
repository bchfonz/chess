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
            else {
//            ServerMessage serverMessage = gson.fromJson(ctx.message(), ServerMessage.class);
                UserGameCommand userGameCommand = gson.fromJson(ctx.message(), UserGameCommand.class);

                switch (userGameCommand.getCommandType()) {
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

    private void connect(String playerName, Session session, int gameID, WsMessageContext ctx, boolean isPlayer, String team) throws IOException {
        connections.add(session);
        NotificationMessage notification;
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        System.out.println("isPlayer = " + isPlayer);
        String joinedMessage = String.format("%s joined the game", playerName);
        String observerMessage = String.format("%s is observing the game", playerName);
        //Checks for either observer or player
        if(isPlayer){
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinedMessage);
            gameServiceObj.joinGame(playerName, team, gameID);
        } else {
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, observerMessage);
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        ctx.send(loadGameMessage.toString());
        connections.broadcast(session, notification);
    }

    private void makeMove(Integer gameID, ChessMove move, String username, WsMessageContext ctx) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        boolean isWhiteTurn = Objects.equals(game.getTeamTurn(), ChessGame.TeamColor.WHITE);
        boolean isPlayerWhite = Objects.equals(username, gameData.whiteUsername());
        if(isWhiteTurn != isPlayerWhite){
            String invalidTurn = "Not your turn!";
            ctx.send(gson.toJson(invalidTurn));
        }
        else {
            try {
                game.makeMove(move);
                GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(gameID, updatedGameData);
                connections.loadGame(game);
                String message = "Move made: " + move;
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(ctx.session, notification);
                if (game.isInCheckmate(game.getTeamTurn())) {
                    String checkmate = game.getTeamTurn() + " is in checkmate!";
                    NotificationMessage checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmate);
                    connections.broadcast(null, checkmateNotification);
                }
                if (game.isInCheck(game.getTeamTurn())) {
                    String check = game.getTeamTurn() + " is in check!";
                    NotificationMessage checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, check);
                    connections.broadcast(null, checkNotification);
                }
                if (game.isInStalemate(game.getTeamTurn())) {
                    String stalemate = "Game ended in stalemate!";
                    NotificationMessage stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, stalemate);
                    connections.broadcast(null, stalemateNotification);
                }
            } catch (InvalidMoveException e) {
                System.out.println("Invalid move exception message: " + e.getMessage());
                ctx.send(gson.toJson(e.getMessage()));
            }
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


//    private void leave(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
}