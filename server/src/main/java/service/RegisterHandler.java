package service;

import spark.*;
import com.google.gson.Gson;

public class RegisterHandler {
    private final Request req;
    private final Response res;
  public RegisterHandler(Request req, Response res){
    this.req = req;
    this. res = res;
  }

  public RegisterRequest getRegisterRequest(){
    RegisterRequest tempRegisterRequest;
    tempRegisterRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
    return tempRegisterRequest;
  }


  
}
