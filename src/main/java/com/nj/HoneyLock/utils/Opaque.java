package com.nj.HoneyLock.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.KeyPair;

public class Opaque {
  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private final OPRF oprf;
  private final AKE ake;

  public Opaque() {
    this.oprf = new OPRF();
    this.ake = new AKE();
  }

  // Step 1: Client blinds the password and sends blinded password + public key to server
  public BigInteger[] clientBlindsPassword(String password) throws Exception {
    return oprf.blindPassword(password);
  }

  // Step 2: Server evaluates OPRF and sends blinded result back to client
  public BigInteger serverEvaluatesOPRF(BigInteger blindedPassword, BigInteger serverSecret) {
    return oprf.evaluateOPRF(blindedPassword, serverSecret);
  }

  // Step 3: Client unblinds result to retrieve the random secret (high-entropy password)
  public BigInteger clientUnblindsResult(BigInteger blindedResult, BigInteger blindingFactor) {
    return oprf.unblindResult(blindedResult, blindingFactor);
  }

  // Step 4: AKE - Generate shared secret key using ECDH
  public byte[] generatesSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
    return ake.generateSharedSecret(privateKey, publicKey);
  }
  // Example workflow of OPAQUE protocol
  public void opaqueProtocol() throws Exception {
    // Client blinds password
    String password = "userpassword123";
    BigInteger[] clientBlindedPassword = clientBlindsPassword(password);

    // Server evaluates OPRF with server's secret key
    BigInteger serverSecret = new BigInteger("123456789");  // Server's secret key (example)
    BigInteger serverResult = serverEvaluatesOPRF(clientBlindedPassword[0], serverSecret);

    // Client unblinds result to get the high-entropy password (rw)
    BigInteger highEntropyPassword = clientUnblindsResult(serverResult, clientBlindedPassword[1]);

    // AKE: Both parties generate key pairs
    KeyPair clientKeyPair = oprf.generateECKeyPair();
    KeyPair serverKeyPair = oprf.generateECKeyPair();

    // Client generates shared secret using AKE (ECDH)
    byte[] clientSharedSecret = generatesSharedSecret(clientKeyPair.getPrivate(), serverKeyPair.getPublic());

    // Server generates shared secret using AKE (ECDH)
    byte[] serverSharedSecret = generatesSharedSecret(serverKeyPair.getPrivate(), clientKeyPair.getPublic());

    // Verify that both parties have derived the same shared secret
    if (java.util.Arrays.equals(clientSharedSecret, serverSharedSecret)) {
      System.out.println("Shared secret established successfully!");
    } else {
      System.out.println("Failed to establish shared secret.");
    }
  }

  public static void main(String[] args) throws Exception {
    Opaque opaque = new Opaque();
    opaque.opaqueProtocol();
  }

}



