package com.nj.HoneyLock.server;

import com.nj.HoneyLock.model.User;
import com.nj.HoneyLock.model.UserRecord;
import com.nj.HoneyLock.utils.HoneywordGenerator;
import com.nj.HoneyLock.utils.Opaque;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

public class OpaqueServer extends Opaque {
  KeyPair serverKeys;
  HoneywordGenerator gen;


  OpaqueServer() throws Exception {
    this.serverKeys = ake.generateECKeyPair();
    gen = new HoneywordGenerator(10);
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
    for (String honeyword : honeywords) {
      BigInteger honeyPrivateKey = he.decrypt(cipher,oprf.generateRW(honeyword,secret));
      PublicKey honeyPublickey = ake.getPublic(ake.retrievePrivateKey(honeyPrivateKey));
      honeyKeys.add(ake.formatPublicKey(honeyPublickey));
    }
    return honeyKeys;
  }

  int suffleKeys(String realKeys, List<String> honeyKeys){
    Random rand = new Random(); // Randomly select an index to insert the real key
    int insertIndex = rand.nextInt(honeyKeys.size() + 1);
    honeyKeys.add(insertIndex, realKeys);
    Collections.shuffle(honeyKeys);  // Shuffle the entire list
    for (int i = 0; i < honeyKeys.size(); i++) {     // Find the index of the real key after shuffling
      if (honeyKeys.get(i).equals(realKeys))  return i;
    }
    return -1;
  }

  void generateServerSK(String userId){
    //find user with user id
    //send the client

  }

  public void saveUserRecord(User user,String pattern) throws Exception {
    List<String> keys = generateHoneyKeys(pattern, user.getSecret(),user.getCipher());
    String realKeys = user.getCipher();
    int index = suffleKeys(realKeys,keys);
    //save the index
    UserRecord newUser = new UserRecord(user.getId(), user.getName(), user.getCipher(), user.getSecret(), keys);
  }



  public static void main(String[] args) throws Exception {
    OpaqueServer s = new OpaqueServer();
    User user = new User("neha",
      "EBIlZx3rltPz3hI5ZFX1v2SjknrsGbul3OkAkjIRPliX0HdZvm7jPef0DtucPRm0bcJgf6zZuQwEw/EdDobSiQ==",
      "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE5RV0/l277oayhBvAjxWzC6ePCpVk23bjkQDDOp/iNpfHMdtPlsg1WnPTpPdYarOJS7Y/Gw/iA/Si5wwaXyCRpA==",
      "1234567890");
   s.saveUserRecord(user,"ADS");
  }
//  void display(ArrayList<ArrayList<String>> l){
//    System.out.println("---------------------------------------------------------------------------");
//    System.out.println(l.size());
//    for(ArrayList<String> list:l){
//      for(String item : list) System.out.print(item+" ");
//      System.out.println();
//    }
//    System.out.println("---------------------------------------------------------------------------");
//  }
}
