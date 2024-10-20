package com.nj.HoneyLock.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class OPRF {

  // Method to generate SHA-256 hash
  byte[] hash(byte[] input) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(input);
  }

  // Method to generate rw as BigInteger
  public BigInteger generateRW(String password, String serverSecret) throws NoSuchAlgorithmException {
    // Hash the password using H₁
    byte[] pwHash = hash(password.getBytes(StandardCharsets.UTF_8));

    // Step 2: Hash the password hash again with server secret
    String pwWithSecret = password + serverSecret;
    byte[] pwSecretHash = hash(pwWithSecret.getBytes(StandardCharsets.UTF_8));

    // Step 3: Combine pwHash and pwSecretHash
    byte[] combined = new byte[pwHash.length + pwSecretHash.length];
    System.arraycopy(pwHash, 0, combined, 0, pwHash.length);
    System.arraycopy(pwSecretHash, 0, combined, pwHash.length, pwSecretHash.length);

    // Final rw generation
    byte[] finalHash = hash(combined);

    // Convert the final hash to a BigInteger
    return new BigInteger(1, finalHash);  // '1' means positive number
  }

}
