package com.nj.HoneyLock.hcServer.repo;

import com.nj.HoneyLock.hcServer.RealIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HCRepository extends MongoRepository<RealIndex,String> {
}
