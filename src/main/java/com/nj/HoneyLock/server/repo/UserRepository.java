package com.nj.HoneyLock.server.repo;

import com.nj.HoneyLock.server.model.UserRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserRecord,String> {
  @Query("{username:?0}")
  Optional<UserRecord> getUserByUsername(String username);
}
