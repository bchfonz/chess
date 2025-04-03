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
				authDAO.createAuth(username);
				String authToken =  authDAO.getAuthData(username).authToken();
				userDAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
				
				res.status(200);
				return new RegisterResult(username, authToken, message);
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
				password = userData.password();
			}
			System.out.println("DAO username: " + username);
			if(userData == null || !loginRequest.password().equals(password)){
				message = "Error: unauthorized";
				res.status(401);
				return new LoginResult(null, null, message);
			}else{
				System.out.println("passwords match");
				message = null;
				authDAO.createAuth(userData.username());
				String authToken =  authDAO.getAuthData(username).authToken();
				res.status(200);
				return new LoginResult(userData.username(), authToken, message);
			}
		}catch(DataAccessException e){
			message = "Error: (description of error)";
			res.status(500);
			return new LoginResult(null, null, message);
		}
		//Check if username is in database
		//Verify password
		//Send response based on previous two comments
	}

	public void logout(LogoutRequest logoutRequest) {}
}