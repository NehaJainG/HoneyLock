package com.nj.HoneyLock.server.model;

public class User {
  String username;
  String name;
  String cipher; //encrypted private key of user
  String publicKey; //public key of user
  String secret;

  public String getUsername() {
    return username;
  }

  public User(String username,String name, String cipher, String publicKey, String secret) {
    this.username =username;
    this.name = name;
    this.cipher = cipher;
    this.publicKey = publicKey;
    this.secret = secret;
  }



  public User(String name){
    this.name = name;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public String getCipher() {
    return cipher;
  }

  public String getName() {
    return name;
  }

  public void setCipher(String cipher) {
    this.cipher = cipher;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  @Override
  public String toString() {
    return "User{" +
      "id='" + username + '\'' +
      "\n, name='" + name + '\'' +
      "\n, cipher='" + cipher + '\'' +
      "\n, publicKey='" + publicKey + '\'' +
      '}';
  }

  public String getUserName() {
    return username;
  }
}
