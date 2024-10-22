package com.nj.HoneyLock.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "UserRecord")
public class UserRecord {
  @Id
  String userId;
  String username;
  String name;
  String cipher;
  String secret;
  List<String> keys;

  public UserRecord(String userId, String username, String name, String cipher, String secret, List<String> keys) {
    this.userId = userId;
    this.username = username;
    this.name = name;
    this.cipher = cipher;
    this.secret = secret;
    this.keys = keys;
  }

  public String getUserName() {
    return username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCipher() {
    return cipher;
  }

  public void setCipher(String cipher) {
    this.cipher = cipher;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public List<String> getKeys() {
    return keys;
  }

  public void setKeys(List<String> keys) {
    this.keys = keys;
  }
}
