package com.nj.HoneyLock.hcServer;

import com.nj.HoneyLock.hcServer.repo.HCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HoneyChecker {
  private final HCRepository hcRepository;

  @Autowired
  public HoneyChecker(HCRepository hcRepository){
    this.hcRepository = hcRepository;
  }

  public void saveUser(String userId, int index){
    //save the record in database
    hcRepository.save(new RealIndex(userId,index));

  }
  public int getIndex(String userId){
    //search user id in database and return the index
    Optional<RealIndex> temp = hcRepository.findById(userId);

    return temp.map(RealIndex::getIndex).orElse(-1);
  }
}
