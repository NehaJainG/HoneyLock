package com.nj.HoneyLock.controller;

import com.nj.HoneyLock.server.model.User;
import com.nj.HoneyLock.server.model.UserRecord;
import com.nj.HoneyLock.service.OpaqueClient;
import com.nj.HoneyLock.service.OpaqueServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private OpaqueServer opaqueServer;

  // Register a new user
  @PostMapping("/register")
  public String registerUser(@RequestBody User userData) throws Exception {
    System.out.println(userData);
    String name = userData.getName();
    String username = userData.getUsername();
    String password = userData.getCipher();
    String secret = userData.getSecret();  // example secret; this could be generated dynamically

    if(opaqueServer.getUserRecord(username) != null){
      return "User already exists";
    }

    OpaqueClient opaqueClient = new OpaqueClient();
    User user = opaqueClient.setUser(name, username, password, secret);
    opaqueServer.saveUserRecord(user, "ADS");
    return "Registration Successful";
  }

  // Authenticate an existing user
  @PostMapping("/login")
  public String authenticateUser(@RequestBody User loginData) throws Exception {
    System.out.println(loginData);
    String username = loginData.getUsername();
    String password = loginData.getCipher();

    User user = new User(username);
    OpaqueClient opaqueClient = new OpaqueClient();

    // Get UserRecord from OpaqueServer
    UserRecord userRecord = opaqueServer.getUserRecord(username);
    if (userRecord == null) {
      return "User not found";
    }
    user = opaqueServer.getUser(user);
    System.out.println(user.toString());
    System.out.println(userRecord.toString());

    // Generate client session key
    byte[] clientSK = opaqueClient.generateClientSK(user, password);
    System.out.println("client" + Arrays.toString(clientSK));
    boolean isAuthenticated = opaqueServer.authenticate(userRecord, clientSK);

    return isAuthenticated ? "Authenticated successfully" : "Authentication failed";
  }
}
