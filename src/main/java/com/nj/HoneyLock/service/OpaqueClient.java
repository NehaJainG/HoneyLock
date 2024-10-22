package com.nj.HoneyLock.service;

import com.nj.HoneyLock.server.model.User;
import com.nj.HoneyLock.utils.Opaque;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class OpaqueClient extends Opaque {
  User user;
  private String secret;
  private final KeyPair clientKeys;

  public OpaqueClient(User user) throws Exception {
    this.clientKeys = ake.generateECKeyPair();
    this.user = user;
  }

  public OpaqueClient() throws Exception {
    this.clientKeys = ake.generateECKeyPair();
  }

  public User setUser(String name, String username, String password, String secret) throws Exception {
    //encrypt the private key
    BigInteger rwd = generateRwd(password,secret);
    BigInteger privateKey = ake.formatPrivateKey((ECPrivateKey) clientKeys.getPrivate());
    String cipher = he.encrypt(privateKey,rwd);

    String publicKey = ake.formatPublicKey((ECPublicKey) clientKeys.getPublic());

    return this.user = new User(username,name,cipher,publicKey,secret);
  }


  public BigInteger generateRwd(String password, String secret) throws NoSuchAlgorithmException {
    this.secret = secret;
    return oprf.generateRW(password,secret);
  }

  public byte[] generateClientSK(User user, String password) throws Exception {
    BigInteger rwd = generateRwd(password,user.getSecret());
    BigInteger privateKey = he.decrypt(user.getCipher(),rwd);
    PrivateKey kU = ake.retrievePrivateKey(privateKey);
    PublicKey KS = ake.retrievePublicKey(user.getPublicKey());
    return  ake.generateSharedSecret(kU,KS);
  }

  public static void main(String[] args) throws Exception {
    OpaqueClient c = new OpaqueClient();
    c.setUser("Neha","neha123","neha@123","1234567890");
    System.out.println(c.user.toString());
  }



}
