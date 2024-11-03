//package com.nj.HoneyLock.controller;
//
//import com.nj.HoneyLock.server.model.SessionKeyRequest;
//import com.nj.HoneyLock.server.model.User;
//import com.nj.HoneyLock.server.model.UserRecord;
//import com.nj.HoneyLock.service.OpaqueServer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//  @Autowired
//  private OpaqueServer opaqueServer;
//
//  // Registration Endpoint
//  @PostMapping("/register")
//  public ResponseEntity<String> register(@RequestBody User request) {
//    System.out.println(request.toString());
//    try {
//      opaqueServer.saveUserRecord(request, "ADS");
//      return ResponseEntity.ok("Registration successful");
//    } catch (Exception e) {
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during registration");
//    }
//  }
//
//  @PostMapping("/login")
//  public ResponseEntity<User> loginUser(@RequestBody User loginRequest) {
//    System.out.println(loginRequest.toString());
//    String username = loginRequest.getUsername();
//    // Fetch user record from database
//    UserRecord userRecord = opaqueServer.getUserRecord(username);
//
//    if (userRecord == null) {
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//    }
//
//    // Send back cipher, secret, and public key
//    User response = new User(
//      loginRequest.getUserName(),
//      loginRequest.getName(),
//      userRecord.getCipher(),
//      opaqueServer.getKServer(),
//      userRecord.getSecret()
//      );
//    return ResponseEntity.ok(response);
//  }
//
//  @PostMapping("/verify")
//  public ResponseEntity<String> verifySessionKey(@RequestBody SessionKeyRequest request) throws Exception {
//    String username = request.getUsername();
//    byte[] clientSessionKey = request.getSessionKey();
//
//    // Fetch user record to generate server session key
//    UserRecord userRecord = opaqueServer.getUserRecord(username);
//
//    // Generate server's session key
//    // Verify if the client session key matches any server session key
//    if (opaqueServer.authenticate(userRecord,clientSessionKey)) {
//      return ResponseEntity.ok("Successfully authenticated!");
//    }
//
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed!");
//  }
//
//}
