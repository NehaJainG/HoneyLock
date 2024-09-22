package com.nj.HoneyLock.utils;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

public class OPRF {

  // Parameters for elliptic curve DH and OPRF
  private static final String CURVE_NAME = "secp256r1";

  // Generate elliptic curve key pair
  public KeyPair generateECKeyPair() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC", "BC");
    ECGenParameterSpec ecSpec = new ECGenParameterSpec(CURVE_NAME);
    keyPairGen.initialize(ecSpec, new SecureRandom());
    return keyPairGen.generateKeyPair();
  }

  // Client: Generate random blinding factor and blind the password
  public BigInteger[] blindPassword(String password) throws Exception {
    KeyPair keyPair = generateECKeyPair();
    BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
    BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

    // Password as integer (for this example)
    BigInteger pwInt = new BigInteger(password.getBytes());
    BigInteger blindingFactor = privateKey.getD(); // Random blinding factor

    // Blind the password by multiplying with the blinding factor
    BigInteger blindedPw = pwInt.multiply(blindingFactor);

    // Return the blinded password and the public key (to send to server)
    return new BigInteger[]{blindedPw, publicKey.getQ().getAffineXCoord().toBigInteger()};
  }

  // Server: Apply secret key to the blinded password
  public BigInteger evaluateOPRF(BigInteger blindedPw, BigInteger serverSecret) {
    // Server applies its secret key to the blinded password
    return blindedPw.multiply(serverSecret);
  }

  // Client: Unblind the result to recover the high-entropy secret
  public  BigInteger unblindResult(BigInteger blindedResult, BigInteger blindingFactor) {
    // Unblind the result by dividing the server's response by the blinding factor
    return blindedResult.divide(blindingFactor);
  }

}
