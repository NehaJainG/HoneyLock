package com.nj.HoneyLock.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

import java.util.Base64;


public class AKE {
  // Parameters for elliptic curve DH and OPRF
  private static final String CURVE_NAME = "secp256r1";
  private static ECParameterSpec ecParameterSpec;
  private static KeyFactory keyFactory;

  // Add BouncyCastle Provider at the start
  static {
    Security.addProvider(new BouncyCastleProvider());
    try {
      // Use BouncyCastle provider for key pair generation
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");

      // Extract ECParameterSpec from the generated key to use as a constant
      ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(CURVE_NAME);
      kpg.initialize(ecGenParameterSpec);
      KeyPair keyPair = kpg.generateKeyPair();
      ecParameterSpec = ((ECPrivateKey) keyPair.getPrivate()).getParams();

      keyFactory = KeyFactory.getInstance("EC", "BC");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Method to generate EC key pair using the constant ECParameterSpec
  public KeyPair generateECKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");  // Use BouncyCastle provider
    keyPairGenerator.initialize(ecParameterSpec);
    return keyPairGenerator.generateKeyPair();
  }

  // Generate shared secret (session key) using ECDH
  public byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
    KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH", "BC");  // Use BouncyCastle provider
    keyAgree.init(privateKey);
    keyAgree.doPhase(publicKey, true);
    return keyAgree.generateSecret();
  }

  // Method to convert ECPrivateKey to BigInteger
  public BigInteger formatPrivateKey(ECPrivateKey privateKey) {
    return privateKey.getS(); // Return the 'S' value as BigInteger
  }

  // Method to convert a BigInteger to PrivateKey using constant ECParameterSpec
  public ECPrivateKey retrievePrivateKey(BigInteger privateKeyValue) throws Exception {
    ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKeyValue, ecParameterSpec);
    KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");  // Use BouncyCastle provider
    return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
  }

  // Convert Public Key to a simple Base64 String
  public String formatPublicKey(ECPublicKey publicKey) {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  // Convert Base64 String back to PublicKey
  public ECPublicKey retrievePublicKey(String publicKeyString) throws Exception {
    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
    return (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
  }

  //derive public key from private key
  public ECPublicKey getPublic(ECPrivateKey privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
    ECParameterSpec params = privateKey.getParams();
    org.bouncycastle.jce.spec.ECParameterSpec bcSpec = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util
      .convertSpec(params);
    org.bouncycastle.math.ec.ECPoint q = bcSpec.getG().multiply(privateKey.getS());
    org.bouncycastle.math.ec.ECPoint bcW = bcSpec.getCurve().decodePoint(q.getEncoded(false));
    ECPoint w = new ECPoint(
      bcW.getAffineXCoord().toBigInteger(),
      bcW.getAffineYCoord().toBigInteger());
    ECPublicKeySpec keySpec = new ECPublicKeySpec(w, params);
    return (ECPublicKey) KeyFactory
      .getInstance("EC", BouncyCastleProvider.PROVIDER_NAME)
      .generatePublic(keySpec);
  }

  public static void main(String[] args) throws Exception {
    AKE ake = new AKE();

    // Generate key pair
    KeyPair keyPair = ake.generateECKeyPair();
    ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
    ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

    System.out.println("Before conversion:");
    System.out.println("Private Key: "+privateKey);
    System.out.println("Public Key "+ publicKey);


    BigInteger strPrivate = ake.formatPrivateKey(privateKey);
    System.out.println("Private Key as BigInteger: " + strPrivate);
    ECPrivateKey conPrivate = ake.retrievePrivateKey(strPrivate);
    System.out.println("Converted Private Key: " + conPrivate);

    String strK = ake.formatPublicKey(publicKey);
    System.out.println("Public Key string " + strK);
    ECPublicKey conK = (ECPublicKey) ake.retrievePublicKey(strK);
    System.out.println("Converted Public Key: " + conK);

    KeyPair newKey = new KeyPair(conK,conPrivate);
    System.out.println("old keys: \n"+keyPair);
    System.out.println("\nnew keys: \n"+newKey);

    PublicKey deriveKey = ake.getPublic(privateKey);
    System.out.println(deriveKey);

  }
}
