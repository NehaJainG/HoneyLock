package com.nj.HoneyLock.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

public class AKE {
  // Parameters for elliptic curve DH and OPRF
  private static final String CURVE_NAME = "secp256r1";
  private static ECParameterSpec ecParameterSpec;

  // Add BouncyCastle Provider at the start
  static {
    Security.addProvider(new BouncyCastleProvider());
    try {
      // Use BouncyCastle provider for key pair generation
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
      ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(CURVE_NAME);
      kpg.initialize(ecGenParameterSpec);
      KeyPair keyPair = kpg.generateKeyPair();

      // Extract ECParameterSpec from the generated key to use as a constant
      ecParameterSpec = ((ECPrivateKey) keyPair.getPrivate()).getParams();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Generate shared secret (session key) using ECDH
  public byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
    KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH", "BC");  // Use BouncyCastle provider
    keyAgree.init(privateKey);
    keyAgree.doPhase(publicKey, true);
    return keyAgree.generateSecret();
  }

  // Method to generate EC key pair using the constant ECParameterSpec
  public KeyPair generateECKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");  // Use BouncyCastle provider
    keyPairGenerator.initialize(ecParameterSpec);  // Use constant ECParameterSpec
    return keyPairGenerator.generateKeyPair();
  }

  // Method to convert a BigInteger to PrivateKey using constant ECParameterSpec
  public PrivateKey intToPrivateKey(BigInteger privateKeyValue) throws Exception {
    ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKeyValue, ecParameterSpec);
    KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");  // Use BouncyCastle provider
    return keyFactory.generatePrivate(privateKeySpec);
  }

  // Method to convert ECPrivateKey to BigInteger
  public BigInteger privateKeyToInt(ECPrivateKey privateKey) {
    return privateKey.getS(); // Return the 'S' value as BigInteger
  }

  // Convert PublicKey to BigInteger coordinates (x and y)
  public BigInteger[] publicKeyToInt(PublicKey publicKey) {
    ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
    ECPoint w = ecPublicKey.getW();
    BigInteger x = w.getAffineX();
    BigInteger y = w.getAffineY();
    return new BigInteger[]{x, y};
  }

  // Convert BigInteger coordinates back to PublicKey
  public PublicKey intToPublicKey(BigInteger x, BigInteger y) throws Exception {
    ECPoint ecPoint = new ECPoint(x, y);
    ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);
    KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
    return keyFactory.generatePublic(publicKeySpec);
  }


  public static void main(String[] args) throws Exception {
    AKE ake = new AKE();

    // Generate key pair
    KeyPair keyPair = ake.generateECKeyPair();

    // Convert ECPrivateKey to BigInteger
    ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
    BigInteger privateKeyValue = ake.privateKeyToInt(privateKey);
    System.out.println("Private Key as BigInteger: " + privateKeyValue);

    // Convert BigInteger back to ECPrivateKey
    ECPrivateKey convertedPrivateKey = (ECPrivateKey) ake.intToPrivateKey(privateKeyValue);
    System.out.println("Converted Private Key: " + convertedPrivateKey);

    // Convert ECPublicKey to BigInteger
    ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
    BigInteger[] publicKeyValues = ake.publicKeyToInt(publicKey);
    System.out.println("Public Key, x: " + publicKeyValues[0]);
    System.out.println("Public key, y: "+publicKeyValues[1]);

    // Convert BigInteger coordinates back to ECPublicKey
    ECPublicKey convertedPublicKey = (ECPublicKey) ake.intToPublicKey(publicKeyValues[0],publicKeyValues[1]);
    System.out.println("Converted Public Key: " + convertedPublicKey);

  }
}
