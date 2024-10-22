package com.nj.HoneyLock.service;

import com.nj.HoneyLock.hcServer.HoneyChecker;
import com.nj.HoneyLock.server.model.User;
import com.nj.HoneyLock.server.model.UserRecord;
import com.nj.HoneyLock.server.repo.UserRepository;
import com.nj.HoneyLock.utils.HoneywordGenerator;
import com.nj.HoneyLock.utils.Opaque;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.*;

@Service
public class OpaqueServer extends Opaque {
  private final UserRepository userRepository;
  private final HoneyChecker hc;
  KeyPair serverKeys;
  HoneywordGenerator gen;

  public OpaqueServer(UserRepository userRepository, HoneyChecker honeyChecker) throws Exception {
    this.userRepository = userRepository;
    this.hc = honeyChecker;
    this.serverKeys = ake.generateECKeyPair();
    this.gen = new HoneywordGenerator(10);
  }

  public String getKServer(){
    return ake.formatPublicKey((ECPublicKey) serverKeys.getPublic());
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
    display1(honeywords);
    System.out.println("***************************************************");

    for (String honeyword : honeywords) {
      BigInteger honeyPrivateKey = he.decrypt(cipher,oprf.generateRW(honeyword,secret));
      ECPublicKey honeyPublickey = ake.getPublic(ake.retrievePrivateKey(honeyPrivateKey));
      String KU = ake.formatPublicKey(honeyPublickey);
      System.out.println(ake.retrievePublicKey(KU));
      honeyKeys.add(KU);
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
    String realKeys = user.getPublicKey();
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
    user.setPublicKey(ake.formatPublicKey((ECPublicKey) serverKeys.getPublic()));
    return user;
  }

  public List<byte[]> generateServerSK(UserRecord user) throws Exception {
    List<byte[]> sessionKeys = new ArrayList<>();
    for (String publicKey: user.getKeys()) {
        PublicKey KU = ake.retrievePublicKey(publicKey);
       byte[] sk = ake.generateSharedSecret(serverKeys.getPrivate(), KU);
      sessionKeys.add(sk);
    }
    display(sessionKeys);
    return sessionKeys;
  }

  public boolean authenticate(UserRecord user, byte[] clientSK) throws Exception {
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

  public static void main(String[] args) throws Exception {
    OpaqueServer s = new OpaqueServer(null,null);
    OpaqueClient c = new OpaqueClient();
    String name = "Dhanush";
    String username = "dj01";
    String password = "hello@12";
    String secret = "1234354462652453525456443";

    //creating user
    System.out.println("User is registering...");
    User newUser = c.setUser(name,username,password,secret);
    UserRecord user = s.saveUserRecord(newUser,"ADS");
    //System.out.println("registration successful\n\n");

    System.out.println("User requested for userRecord..");

    System.out.println("authorised user....");
    System.out.println("Authentication Success...");

    byte[] clientSK = c.generateClientSK(newUser,password);
    //UserRecord user = s.getUserRecord(username);

    if(s.authenticate(user,clientSK)){
      System.out.println("Authentication successfull...");
    }else{
      System.out.println("Authentication failed, password incorrect");
    }
  }
  private void display1(List<String> list){
    for (String s : list) {
      System.out.println(s);
    }
  }
  private void display(List<byte[]> list){
    for (byte[] s : list) {
      System.out.println(Arrays.toString(s));
    }
  }

}
