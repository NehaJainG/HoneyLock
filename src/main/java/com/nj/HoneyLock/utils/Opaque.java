package com.nj.HoneyLock.service.utils;

public class Opaque {

  protected final OPRF oprf;
  protected final AKE ake;
  protected final HoneyEncryption he;


  public Opaque() {
    this.oprf = new OPRF();
    this.ake = new AKE();
    this.he = new HoneyEncryption();
  }
    /*
    workflow of OPAQUE protocol
       AKE: Both parties generate key pairs
    Step 1: Client blinds the password and sends blinded password + public key to server
    Step 2: Server evaluates OPRF and sends blinded result back to client
    Step 3: Client unblinds result to retrieve the random secret (high-entropy password), rw
            encrypt the private key--> c
    Step 4: Server sends c, public key of client stored
    Step 5: AKE - Generate shared secret key using ECDH for both
  */

}



