package service;

import dataaccess.*;

public class ClearService {
    private final SqlUserDAO userDAO = new SqlUserDAO();
    private final SqlAuthDAO authDAO = new SqlAuthDAO();
    private final SqlGameDAO gameDAO = new SqlGameDAO();
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
