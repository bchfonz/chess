# My notes


PHASE 3 summary:

Endpoints: 7 of them
1. Clear application
2. Register
3. Login
4. Logout
5. List Games
6. Create Game
7. Join Game

DATA:

Data Model Classes: Models for the different data I will deal with
UserData
GameData
AuthData

DataAccessClasses: Represents access to db
Create
Read
Update
Delete

Data Access Methods:
clear
createUser
getUser
createGame
getGame
listGames
updateGame
createAuth
getAuth
deleteAuth

SERVICE:

Service Classes: Implements logic of the server
i.e.
public class UserService {
	public RegisterResult register(RegisterRequest registerRequest) {}
	public LoginResult login(LoginRequest loginRequest) {}
	public void logout(LogoutRequest logoutRequest) {}
}

Request and Result Classes: Service classes deal with requests and return results

