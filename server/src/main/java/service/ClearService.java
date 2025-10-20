package service;

import dataaccess.*;

public class ClearService {
    private final LocalUserDB userDAO = new LocalUserDB();
    private final LocalAuthDB authDAO = new LocalAuthDB();
    private final LocalGameDB gameDAO = new LocalGameDB();
    public void clearAllDB(UserService userServiceObj, GameService gameServiceObj) {
        try{
            userServiceObj.authDAO.clearAuthDB();
            userServiceObj.userDAO.clearUserDB();
            gameServiceObj.gameDAO.clearGameDB();
        }catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isCleared(){
        return userDAO.emptyDB() && authDAO.emptyDB() && gameDAO.emptyDB();
    }


}
