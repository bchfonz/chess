package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import spark.*;
public class UserService {

	private final MemoryUserDAO userDAO = new MemoryUserDAO();
	private final MemoryAuthDAO authDAO = new MemoryAuthDAO();

	public RegisterResult register(RegisterRequest registerRequest, Response res) {
		String message;
		try{
			if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
				res.status(400);
				message = "Error: bad request";
				return new RegisterResult(null, null, message);
			}
			if(userDAO.getUser(registerRequest.username()) != null){
				res.status(403);
				message = "Error: already taken";
				return new RegisterResult(null, null, message);
			}else{
				message = null;
				String authToken =  authDAO.createAuth(registerRequest.username()).authToken();
				String username = registerRequest.username();
				userDAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
				authDAO.createAuth(username);
				res.status(200);
				return new RegisterResult(username, authToken, message);
			}
		}catch(DataAccessException e){
			return null;
		}
	}

	public LoginResult login(LoginRequest loginRequest) {
		LoginResult tempLoginRequest = new LoginResult(null, null);
		return tempLoginRequest;
	}

	public void logout(LogoutRequest logoutRequest) {}
}