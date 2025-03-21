package service;


public class UserService {
	public RegisterResult register(RegisterRequest registerRequest) {
		RegisterResult tempRegisterResult = new RegisterResult(registerRequest.username(), null);
		return tempRegisterResult;
	}	
	public LoginResult login(LoginRequest loginRequest) {
		LoginResult tempLoginRequest = new LoginResult(null, null);
		return tempLoginRequest;
	}
	public void logout(LogoutRequest logoutRequest) {}
}