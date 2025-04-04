package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import model.AuthData;
import spark.*;
public class UserService {

	private final MemoryUserDAO userDAO = new MemoryUserDAO();
	private final MemoryAuthDAO authDAO = new MemoryAuthDAO();

	public RegisterResult register(RegisterRequest registerRequest, Response res) {
		String message = null;
		try{
			if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
				res.status(400);
				message = "Error: bad request";
				return new RegisterResult(null, null, message);
			}
			else if(userDAO.getUser(registerRequest.username()) != null){
				res.status(403);
				message = "Error: already taken";
				return new RegisterResult(null, null, message);
			}else{
				String username = registerRequest.username();
				AuthData tempAuthData = authDAO.createAuth(username);
				// String authToken =  tempAuthData.authToken();
				userDAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
				
				res.status(200);
				return new RegisterResult(username, tempAuthData.authToken(), message);
			}
		}catch(DataAccessException e){
			message = "Error: (description of error)";
			res.status(500);
			return new RegisterResult(null, null, message);
		}
	}

	public LoginResult login(LoginRequest loginRequest, Response res) {
		String message;
		String username = null;
		String password = null;
		try{
			System.out.println("Attempted username: " + loginRequest.username());
			System.out.println("Attempted password: " + loginRequest.password());
			UserData userData = userDAO.getUser(loginRequest.username());
			if(userData != null){
				username = userData.username();
				System.out.println("Attempted username 2: " + username);
				password = userData.password();
				System.out.println("Attempted password 2: " + password);
			}
			System.out.println("DAO username: " + username);
			if(userData == null || !loginRequest.password().equals(password)){
				message = "Error: unauthorized";
				res.status(401);
				return new LoginResult(null, null, message);
			}else{
				System.out.println("passwords match");
				message = null;
				AuthData tempAuthData = authDAO.createAuth(userData.username());
				String authToken =  tempAuthData.authToken();
				res.status(200);
				return new LoginResult(userData.username(), authToken, message);
			}
		}catch(DataAccessException e){
			message = "Error: (description of error)";
			res.status(500);
			return new LoginResult(null, null, message);
		}
	}

	public LogoutResult logout(LogoutRequest logoutRequest, Response res) {
		try{
			if(authDAO.deleteAuth(logoutRequest.authToken())){
				res.status(200);
				return new LogoutResult(null);
				
			}else{
				res.status(401);
				return new LogoutResult("Error: unauthorized");
			}
		}catch(DataAccessException e){
			res.status(500);
			return new LogoutResult("Error: (description of error)");
		}
		
	}
}