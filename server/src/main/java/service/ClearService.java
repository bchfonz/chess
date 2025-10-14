package service;

import dataaccess.AuthDAO;
import dataaccess.localAuthDB;
import dataaccess.localUserDB;
import dataaccess.localGameDB;

public class ClearService {
    private final localUserDB userDAO = new localUserDB();
    private final localAuthDB authDAO = new localAuthDB();
    private final localGameDB gameDAO = new localGameDB();
    public void clearAllDB (){
        userDAO.clearUserDB();
        authDAO.clearAuthDB();
        gameDAO.clearGameDB();
    }
    public boolean isCleared(){
        return userDAO.emptyDB() && authDAO.emptyDB() && gameDAO.emptyDB();
    }
}
