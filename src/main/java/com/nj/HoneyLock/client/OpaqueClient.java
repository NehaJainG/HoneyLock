package com.nj.HoneyLock.client;

import com.nj.HoneyLock.model.User;
import com.nj.HoneyLock.utils.HoneywordGenerator;
import com.nj.HoneyLock.utils.Opaque;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;

public class OpaqueClient extends Opaque {
  User user;
  private BigInteger rwd;
  KeyPair clientKeys;
  HoneywordGenerator gen;
  ArrayList<String> honeywords;

  public OpaqueClient(User user) throws Exception {
    this.clientKeys = ake.generateECKeyPair();
    this.user = user;
    this.gen = new HoneywordGenerator(19,4);
  }

  //1. calculate rw- key for encryption and decryption.
  public void calculateOPRF(String password, BigInteger s) throws Exception{
    BigInteger[] blindPwd = oprf.blindPassword(password,clientKeys);
    BigInteger blindRwd = oprf.evaluateOPRF(blindPwd[0],s);
    this.rwd = oprf.unblindResult(blindRwd,blindPwd[1]);
  }

  //2. send U,s,c,KU(user id, secret key, cipher(private key), public key
  public User sendToServerRegister(BigInteger s) throws Exception {
    BigInteger privateKey = ake.privateKeyToInt((ECPrivateKey) clientKeys.getPrivate());
    String c = he.encrypt(privateKey,rwd);
    user.setPublicKey(ake.publicKeyToInt(clientKeys.getPublic()));
    user.setCipher(c);
    return user;
  }

  //3. generate hoeny keys
  public BigInteger[][] generateHoneykeys(String password) throws Exception {
    this.honeywords = gen.generateValidHoneywords(password);

    //printing hoenywords in console
    System.out.println("Generated Honeywords:");
    for (String honeyword : honeywords) {
      System.out.println(honeyword);
    }

    BigInteger[][] honeykeys = new BigInteger[19][2];
    for(int i =0; i<19; i++){
      honeykeys[i] = ake.publicKeyToInt(ake.generateECKeyPair().getPublic());
    }
    return honeykeys;
  }

  //ake
  public byte[] generateSK(String cipher, BigInteger[] serverKey) throws Exception {
    PrivateKey kU = ake.intToPrivateKey(he.decrypt(cipher,this.rwd));
    PublicKey KS = ake.intToPublicKey(serverKey[0],serverKey[1]);
    return ake.generateSharedSecret(kU,KS);
  }

  public User getUser() {
    return user;
  }

  public BigInteger getRwd() {
    return rwd;
  }

  public KeyPair getClientKeys() {
    return clientKeys;
  }
}
