package com.nj.HoneyLock.controller;

import com.nj.HoneyLock.server.model.User;
import com.nj.HoneyLock.server.model.UserRecord;
import com.nj.HoneyLock.service.OpaqueClient;
import com.nj.HoneyLock.service.OpaqueServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private OpaqueServer opaqueServer;

  // Register a new user
  @PostMapping("/register")
  public UserRecord registerUser(@RequestBody HashMap<String, String> userData) throws Exception {
    System.out.println(userData);
    String name = userData.get("name");
    String username = userData.get("username");
    String password = userData.get("password");
    String secret = "1234567890";  // example secret; this could be generated dynamically

    OpaqueClient opaqueClient = new OpaqueClient();
    User user = opaqueClient.setUser(name, username, password, secret);
    return opaqueServer.saveUserRecord(user, "ADS"); // Save user with a specified pattern
  }

  // Authenticate an existing user
  @PostMapping("/authenticate")
  public String authenticateUser(@RequestBody HashMap<String, String> loginData) throws Exception {
    String username = loginData.get("username");
    String password = loginData.get("password");

    User user = new User(username);
    OpaqueClient opaqueClient = new OpaqueClient();

    // Get UserRecord from OpaqueServer
    UserRecord userRecord = opaqueServer.getUserRecord(username);
    if (userRecord == null) {
      return "User not found";
    }
    user = opaqueServer.getUser(user);

    // Generate client session key
    byte[] clientSK = opaqueClient.generateClientSK(user, password);
    boolean isAuthenticated = opaqueServer.authenticate(userRecord, clientSK);

    return isAuthenticated ? "Authenticated successfully" : "Authentication failed";
  }
}
