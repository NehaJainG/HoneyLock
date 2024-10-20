package com.nj.HoneyLock.service;

import com.nj.HoneyLock.hcServer.HoneyChecker;
import com.nj.HoneyLock.server.model.User;
import com.nj.HoneyLock.server.model.UserRecord;
import com.nj.HoneyLock.server.repo.UserRepository;
import com.nj.HoneyLock.utils.HoneywordGenerator;
import com.nj.HoneyLock.utils.Opaque;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

public class OpaqueServer extends Opaque {
  @Autowired
  private UserRepository userRepository;
  KeyPair serverKeys;
  HoneywordGenerator gen;
  HoneyChecker hc;

  public OpaqueServer() throws Exception {
    this.serverKeys = ake.generateECKeyPair();
    gen = new HoneywordGenerator(10);
    hc=new HoneyChecker();
  }


  /*
  * { userid : id,
  *   name : name,
  *   cipher : '---'
  *   secret : "  "
  *   keys : Array(KU)
  * }
  * */

  private List<String> generateHoneyKeys(String pattern, String secret, String cipher) throws Exception {
    List<String> honeyKeys = new ArrayList<>();
    List<String> honeywords = gen.generateHoneywords(pattern);
    System.out.println("***************************************************");
    System.out.println("HONEY WORDS:");
    display(honeywords);
    System.out.println("***************************************************");

    for (String honeyword : honeywords) {
      BigInteger honeyPrivateKey = he.decrypt(cipher,oprf.generateRW(honeyword,secret));
      PublicKey honeyPublickey = ake.getPublic(ake.retrievePrivateKey(honeyPrivateKey));
      honeyKeys.add(ake.formatPublicKey(honeyPublickey));
    }
    return honeyKeys;
  }

  int shuffleKeys(String realKeys, List<String> honeyKeys){
    Random rand = new Random(); // Randomly select an index to insert the real key
    int insertIndex = rand.nextInt(honeyKeys.size() + 1);
    honeyKeys.add(insertIndex, realKeys);
    Collections.shuffle(honeyKeys);  // Shuffle the entire list
    for (int i = 0; i < honeyKeys.size(); i++) {     // Find the index of the real key after shuffling
      if (honeyKeys.get(i).equals(realKeys))  return i;
    }
    return -1;
  }

  public UserRecord saveUserRecord(User user,String pattern) throws Exception {
    List<String> keys = generateHoneyKeys(pattern, user.getSecret(),user.getCipher());
    String realKeys = user.getCipher();
    int index = shuffleKeys(realKeys,keys);
    //save the index
    hc.saveUser(user.getUsername(), index);
    UserRecord newUser = new UserRecord(null, user.getUsername(), user.getName(), user.getCipher(), user.getSecret(), keys);
    //save the user
    userRepository.save(newUser);
    return newUser;
  }

  public UserRecord getUserRecord(String username){
    //find user with user id
    Optional<UserRecord> user = userRepository.getUserByUsername(username);
    return user.orElse(null);
  }

  //send cipher and server public key for session key generation
  public User getUser(User user){
    UserRecord userRecord = getUserRecord(user.getUserName());
    user.setCipher(userRecord.getCipher());
    user.setPublicKey(ake.formatPublicKey(serverKeys.getPublic()));
    return user;
  }

  List<byte[]> generateServerSK(UserRecord user) throws Exception {
    List<byte[]> sessionKeys = new ArrayList<>();
    for (String publicKey: user.getKeys()) {
        PublicKey KU = ake.retrievePublicKey(publicKey);
       byte[] sk = ake.generateSharedSecret(serverKeys.getPrivate(), KU);
      sessionKeys.add(sk);
    }
    return sessionKeys;
  }

  boolean authenticate(UserRecord user, byte[] clientSK) throws Exception {
    List<byte[]> serverSKs = generateServerSK(user);
    int realIndex = hc.getIndex(user.getUserName());
    for (int i = 0; i < serverSKs.size() ; i++) {
      if (Arrays.equals(clientSK, serverSKs.get(i))){
        if(realIndex==i) System.out.println("\nSuccessfully authenticated...\n");
        else System.out.println("\npassword leakage detected...\n");
        return true;
      }
    }
    System.out.println("\nAuthentication failed...\n");
    return false;
  }

//  public static void main(String[] args) throws Exception {
//    OpaqueServer s = new OpaqueServer();
//    User user = new User("neha123","neha",
//      "EBIlZx3rltPz3hI5ZFX1v2SjknrsGbul3OkAkjIRPliX0HdZvm7jPef0DtucPRm0bcJgf6zZuQwEw/EdDobSiQ==",
//      "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE5RV0/l277oayhBvAjxWzC6ePCpVk23bjkQDDOp/iNpfHMdtPlsg1WnPTpPdYarOJS7Y/Gw/iA/Si5wwaXyCRpA==",
//      "1234567890");
//   s.saveUserRecord(user,"ADS");
//  }
  private void display(List<String> list){
    for (String s : list) {
      System.out.println(s);
    }
  }
}
