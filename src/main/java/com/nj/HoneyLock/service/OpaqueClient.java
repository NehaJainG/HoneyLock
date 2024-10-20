package com.nj.HoneyLock.client;

import com.nj.HoneyLock.model.User;
import com.nj.HoneyLock.utils.Opaque;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;

public class OpaqueClient extends Opaque {
  User user;
  private BigInteger rwd;
  private String secret;
  private final KeyPair clientKeys;

  public OpaqueClient(User user) throws Exception {
    this.clientKeys = ake.generateECKeyPair();
    this.user = user;
  }

  public OpaqueClient() throws Exception {
    this.clientKeys = ake.generateECKeyPair();
    this.user = new User("neha");
  }

  public void generateRwd(String password, String secret) throws NoSuchAlgorithmException {
    this.secret = secret;
    this.rwd = oprf.generateRW(password,secret);
  }

  public User generateUserRecord() throws Exception {
    BigInteger privateKey = ake.formatPrivateKey((ECPrivateKey) clientKeys.getPrivate());
    String publicKey = ake.formatPublicKey(clientKeys.getPublic());

    //encrypt the private key
    String cipher = he.encrypt(privateKey,this.rwd);

    this.user.setCipher(cipher);
    this.user.setPublicKey(publicKey);
    this.user.setSecret(this.secret);

    return this.user;
  }

  public byte[] generateClientSK(User userRecord) throws Exception {
     BigInteger privateKey = he.decrypt(userRecord.getCipher(),this.rwd);
    PrivateKey kU = ake.retrievePrivateKey(privateKey);
    PublicKey KS = ake.retrievePublicKey(user.getPublicKey());
    return  ake.generateSharedSecret(kU,KS);
  }



  public static void main(String[] args) throws Exception {
  OpaqueClient c = new OpaqueClient();
  c.generateRwd("neha@1234","1234567890");
  c.generateUserRecord();
    System.out.println(c.user.toString());
  }



}
