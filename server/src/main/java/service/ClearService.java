package service;

import dataaccess.*;

public class ClearService {
    private final localUserDB userDAO = new localUserDB();
    private final localAuthDB authDAO = new localAuthDB();
    private final localGameDB gameDAO = new localGameDB();
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
