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

Data Model Classes: Models for the different data I will deal with\
UserData\
GameData\
AuthData

DataAccessClasses: Represents access to db\
Create\
Read\
Update\
Delete

Data Access Methods:\
clear\
createUser\
getUser\
createGame\
getGame\
listGames\
updateGame\
createAuth\
getAuth\
deleteAuth

SERVICE:

Service Classes: Implements logic of the server\
i.e.\
public class UserService {
	public RegisterResult register(RegisterRequest registerRequest) {}\
	public LoginResult login(LoginRequest loginRequest) {}\
	public void logout(LogoutRequest logoutRequest) {}\
}

Request and Result Classes: Service classes deal with requests and return results

Serialization:\
Gson serializer translates between Java and JSON

CURL:

| Command                                                                                    | What It Does                             |
| ------------------------------------------------------------------------------------------ | ---------------------------------------- |
| `curl http://localhost:8080/user`                                                          | GET request                              |
| `curl -X POST http://localhost:8080/user`                                                  | POST request                             |
| `curl -X POST -d '{"a":1}' -H "Content-Type: application/json" http://localhost:8080/user` | Send JSON                                |
| `curl -i http://localhost:8080/user`                                                       | Show headers                             |
| `curl -v http://localhost:8080/user`                                                       | Verbose (shows request/response details) |

