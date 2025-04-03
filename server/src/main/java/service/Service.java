package service;

import dataaccess.*;

public class Service {

  private final MemoryAuthDAO memoryAuthDAO;
  private final MemoryGameDAO memoryGameDAO;
  private final MemoryUserDAO memoryUserDAO;
  public Service(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO, MemoryUserDAO memoryUserDAO){
    this.memoryAuthDAO = memoryAuthDAO;
    this.memoryGameDAO = memoryGameDAO;
    this.memoryUserDAO = memoryUserDAO;
  }

  public void clearAllData(){
    memoryAuthDAO.clearAuth();
    memoryGameDAO.clearGames();
    memoryUserDAO.clearUsers();
  }

}
