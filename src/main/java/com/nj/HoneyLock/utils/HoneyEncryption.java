
package com.nj.HoneyLock.utils;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HoneyEncryption {
  private static BigInteger m2;

  public HoneyEncryption(){
    m2 = new BigInteger("1234567890"); //just for demo
  }
  // Parameters
  private static final int ENCODING_BITS = 256;  // Bit length for encoding message

  // Ensure the byte array for r_w is 16 bytes (for AES-128)
  private static byte[] getFixedLengthKey(BigInteger r_w) {
    byte[] keyBytes = r_w.toByteArray();
    byte[] fixedLengthKey = new byte[16];  // AES-128 requires 16 bytes

    if (keyBytes.length > 16) {
      // If the byte array is longer than 16 bytes, truncate it
      System.arraycopy(keyBytes, 0, fixedLengthKey, 0, 16);
    } else if (keyBytes.length < 16) {
      // If the byte array is shorter than 16 bytes, pad it with leading zeros
      System.arraycopy(keyBytes, 0, fixedLengthKey, 16 - keyBytes.length, keyBytes.length);
    } else {
      // If it's already 16 bytes, use as is
      fixedLengthKey = keyBytes;
    }
    return fixedLengthKey;
  }
  // Encode the message (probabilistic encoding)
  private static BigInteger encodeMessage(BigInteger kU, BigInteger m2) {
    BigInteger l = BigInteger.valueOf(ENCODING_BITS);
    BigInteger intervalStart = kU.multiply(BigInteger.TWO.pow(l.intValue())).divide(m2);
    BigInteger intervalEnd = (kU.add(BigInteger.ONE)).multiply(BigInteger.TWO.pow(l.intValue())).divide(m2);
    SecureRandom random = new SecureRandom();
    return intervalStart.add(BigInteger.valueOf(random.nextInt(intervalEnd.subtract(intervalStart).intValue())));
  }

  // Decode the message (recover kU from the encoded message)
  private static BigInteger decodeMessage(BigInteger encodedMessage, BigInteger m2) {
    BigInteger l = BigInteger.valueOf(ENCODING_BITS);
    return encodedMessage.multiply(m2).divide(BigInteger.TWO.pow(l.intValue()));
  }

  // Encrypt the encoded message (S) using AES and rw (derived from OPRF)
  private static String encryptAES(BigInteger encodedMessage, BigInteger r_w) throws Exception {
    // Ensure r_w is 16 bytes for AES-128
    byte[] keyBytes = getFixedLengthKey(r_w);
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    byte[] cipherText = cipher.doFinal(encodedMessage.toByteArray());
    return Base64.getEncoder().encodeToString(cipherText);
  }

  // Decrypt the ciphertext using AES and rw (derived from OPRF)
  private static BigInteger decryptAES(String cipherText, BigInteger r_w) throws Exception {
    // Ensure r_w is 16 bytes for AES-128
    byte[] keyBytes = getFixedLengthKey(r_w);
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, keySpec);
    byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
    return new BigInteger(plainText);
  }

  public String encrypt(BigInteger kU, BigInteger r_w) throws Exception{
    BigInteger encodedMessage = encodeMessage(kU, m2);
    return encryptAES(encodedMessage,r_w);
  }
  public BigInteger decrypt(String cipherText, BigInteger r_w) throws Exception{
    BigInteger decMessage = decryptAES(cipherText, r_w);
    return decodeMessage(decMessage, m2);
  }

  public static void main(String[] args) {
    try {
      // Sample private key (kU) and domain parameters (Zm2)
      BigInteger kU = new BigInteger("987654321");  // Example private key
      // Assume r_w is derived from the OPRF process
      BigInteger r_w = new BigInteger("12345678901234567890");  // Example rw from OPRF

      // Client-side: Encode and encrypt the private key using rw
      System.out.println("Client: Encoding and encrypting the private key.");
      BigInteger encodedMessage = encodeMessage(kU, m2);
      String cipherText = encryptAES(encodedMessage, r_w);
      System.out.println("Encrypted private key: " + cipherText);

      // Client-side: Decrypt the private key with the correct key rw
      System.out.println("Client: Decrypting the private key using rw.");
      BigInteger decryptedEncodedMessage = decryptAES(cipherText, r_w);
      BigInteger decryptedPrivateKey = decodeMessage(decryptedEncodedMessage, m2);
      System.out.println("Decrypted private key: " + decryptedPrivateKey);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
