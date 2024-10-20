package com.nj.HoneyLock;

import com.nj.HoneyLock.service.OpaqueClient;
import com.nj.HoneyLock.service.OpaqueServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication()
public class HoneyLockApplication implements CommandLineRunner {

	public static void main(String[] args) {

    SpringApplication.run(HoneyLockApplication.class, args);
	}

  @Override
  public void run(String... args) throws Exception {
    System.out.println("************************************************************");
    System.out.println("Start creating and printing mongo objects");
    System.out.println("************************************************************");

    OpaqueServer server = new OpaqueServer();
    OpaqueClient client = new OpaqueClient();

    System.out.println("************************************************************");
    System.out.println("Ended printing mongo objects");
    System.out.println("************************************************************");

  }
}
