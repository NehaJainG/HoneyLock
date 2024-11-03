package com.nj.HoneyLock;

import com.nj.HoneyLock.hcServer.HoneyChecker;
import com.nj.HoneyLock.hcServer.repo.HCRepository;
import com.nj.HoneyLock.server.repo.UserRepository;
import com.nj.HoneyLock.service.OpaqueClient;
import com.nj.HoneyLock.service.OpaqueServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class HoneyLockApplication implements CommandLineRunner {
  @Autowired
  UserRepository userRepository;
  @Autowired
  HCRepository hcRepository;

	public static void main(String[] args) {

    SpringApplication.run(HoneyLockApplication.class, args);
	}

  @Override
  public void run(String... args) throws Exception {
    System.out.println("************************************************************");
    System.out.println("Start creating and printing mongo objects");
    System.out.println("************************************************************");

    HoneyChecker hc = new HoneyChecker(hcRepository);
    OpaqueServer server = new OpaqueServer(userRepository,hc);
    OpaqueClient client = new OpaqueClient();

    System.out.println("************************************************************");
    System.out.println("Ended printing mongo objects");
    System.out.println("************************************************************");

  }
}
