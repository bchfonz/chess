package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;

import jakarta.websocket.*;
import ui.GameplayUI;
import ui.PrintBoard;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    private final Gson gson;
    PrintBoard boardPrinter = new PrintBoard();
    boolean whiteTeam = true;

    public WebSocketFacade(String url) throws ResponseException {
        this.gson = new Gson();
        try {
//          Establish WebSocket connection
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    if(message.contains("NOTIFICATION")){
                        NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                        System.out.println(notificationMessage.getNotificationMessage());
                    }
                    if(message.contains("LOAD_GAME")){
                        //Handle load game message
                        LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                        ChessGame game = loadGameMessage.getGame();
                        if(whiteTeam){
                            boardPrinter.boardWhite(game);
                        } else {
                            boardPrinter.boardBlack(game);
                        }

                    }
                    if(message.contains("ERROR")){
                        ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                        System.out.println("Error: " + errorMessage.getErrorMessage());
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(UserGameCommand.CommandType commandType, String authToken, int gameID, String team, boolean isPlayer) throws ResponseException {
        try {
            ConnectCommand command = new ConnectCommand(commandType, authToken, gameID, isPlayer, team);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            if(team != null && isPlayer) {
                whiteTeam = Objects.equals(team, "WHITE");
            }
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(UserGameCommand.CommandType commandType, String authToken, int gameID, ChessMove move) throws ResponseException {
        //Create websocket message to send to the websocket server
        try {
            MakeMoveCommand command = new MakeMoveCommand(commandType, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leave(UserGameCommand.CommandType commandType, String authToken, int gameID) throws ResponseException {
        //Create websocket message to send to the websocket server
        try {
            UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

//    public void leave(UserGameCommand userGameCommand) throws IOException {
//        this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
//
//    }

}

