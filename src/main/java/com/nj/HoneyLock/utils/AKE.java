package com.nj.HoneyLock.utils;

import javax.crypto.KeyAgreement;
import java.security.PrivateKey;
import java.security.PublicKey;


public class AKE {
  // Generate shared secret (session key) using ECDH
  public byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
    KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH", "BC");
    keyAgree.init(privateKey);
    keyAgree.doPhase(publicKey, true);
    return keyAgree.generateSecret();
  }
}
