package com.nj.HoneyLock.server;

import com.nj.HoneyLock.model.User;
import com.nj.HoneyLock.utils.Opaque;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

public class OpaqueServer extends Opaque {
  KeyPair serverKeys;
  User user;
  BigInteger[][] honeyKeys;

  public BigInteger getS() {
    return serverSecret;
  }

  BigInteger serverSecret = new BigInteger("123456789");

  public OpaqueServer() throws Exception {
    this.serverKeys = ake.generateECKeyPair();
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setHoneyKeys(BigInteger[][] honeyKeys) {
    this.honeyKeys = honeyKeys;
  }

  public BigInteger[] getPublicKey(){
    return ake.publicKeyToInt(serverKeys.getPublic());
  }

  public ArrayList<byte[]> generateSKs() throws Exception {
    ArrayList<byte[]> sessionKeys = new ArrayList<>();
    PublicKey KU = ake.intToPublicKey(user.getPublicKey()[0],user.getPublicKey()[1]);
    sessionKeys.add(ake.generateSharedSecret(serverKeys.getPrivate(), KU));
    for (int i = 1; i<20;i++) {
      PublicKey honeyKU = ake.intToPublicKey(honeyKeys[i-1][0],honeyKeys[i-1][1]);
      sessionKeys.add(ake.generateSharedSecret(serverKeys.getPrivate(), honeyKU));
    }
    return sessionKeys;
  }

  public KeyPair getServerKeys() {
    return serverKeys;
  }

  public User getUser() {
    return user;
  }

  public BigInteger[][] getHoneyKeys() {
    return honeyKeys;
  }
}
