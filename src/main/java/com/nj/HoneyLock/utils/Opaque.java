package com.nj.HoneyLock.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;

public class Opaque {
  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  protected final OPRF oprf;
  protected final AKE ake;
  protected final HoneyEncryption he;


  public Opaque() {
    this.oprf = new OPRF();
    this.ake = new AKE();
    this.he = new HoneyEncryption();
  }
  // Step 1: Client blinds the password and sends blinded password + public key to server
  // Step 2: Server evaluates OPRF and sends blinded result back to client
  // Step 3: Client unblinds result to retrieve the random secret (high-entropy password), rw
  // Step 4: Server sends c, public key of client stored
  // Step 4: AKE - Generate shared secret key using ECDH for both

  // Example workflow of OPAQUE protocol
  private void opaqueProtocol() throws Exception {
    // AKE: Both parties generate key pairs
    KeyPair clientKeyPair = ake.generateECKeyPair();
    KeyPair serverKeyPair = ake.generateECKeyPair();

    // Client blinds password
    String password = "userpassword123";
    BigInteger serverSecret = new BigInteger("123456789");  // Server's secret key (example)

    //create rw
    BigInteger[] blindPwd = oprf.blindPassword(password,clientKeyPair);
    BigInteger blindRwd = oprf.evaluateOPRF(blindPwd[0],serverSecret);
    BigInteger rwd = oprf.unblindResult(blindRwd,blindPwd[1]);

    //encrypt the private key
    BigInteger privateKey = ake.privateKeyToInt((ECPrivateKey) clientKeyPair.getPrivate());
    String cipher = he.encrypt(privateKey,rwd);

    //private key, public key of client is sent to server. Stored in server
    System.out.println("keys sent to server. and gets stored");

    //client decrypts the keys sent by server
    BigInteger privateKeyD = he.decrypt(cipher,rwd);
    System.out.println("before encryption: "+privateKey + "\ndecryted: " + privateKeyD);
    PrivateKey clientPrivate = ake.intToPrivateKey(privateKeyD);
    System.out.println(clientPrivate.toString());

    BigInteger[] publicKey = ake.publicKeyToInt(serverKeyPair.getPublic());
    PublicKey serverPublic = ake.intToPublicKey(publicKey[0],publicKey[1]);

    //sessions generation
    byte[] clientSK = ake.generateSharedSecret(clientPrivate, serverPublic);
    byte[] serverSK = ake.generateSharedSecret(serverKeyPair.getPrivate(), clientKeyPair.getPublic());
    System.out.println("Client session key: ");
    display(clientSK);
    System.out.println("Server session key: ");
    display(serverSK);
    System.out.println(clientKeyPair.getPrivate());
    System.out.println(clientKeyPair.getPublic());



    if (java.util.Arrays.equals(clientSK,serverSK)){
      System.out.println("authenticated");
    }else{
      System.out.println("Not Succesful");
    }


  }

  public static void display(byte[] sk){
    for(byte i : sk){
      System.out.print(i);
    }
    System.out.println();
  }

  public static void main(String[] args) throws Exception {
    Opaque opaque = new Opaque();
    opaque.opaqueProtocol();
  }

}



