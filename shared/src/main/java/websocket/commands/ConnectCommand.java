package websocket.commands;

public class ConnectCommand  extends  UserGameCommand{
    private final boolean isPlayer;
    private final String team;
    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, boolean isPlayer, String team) {
        super(commandType, authToken, gameID);
        this.isPlayer = isPlayer;
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
}
