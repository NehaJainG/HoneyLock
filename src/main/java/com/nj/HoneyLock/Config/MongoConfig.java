package com.nj.HoneyLock.Config;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

  @Primary
  @Bean(name = "serverProperties")
  @ConfigurationProperties(prefix = "spring.data.mongodb.server")
  public MongoProperties getServerProps() throws Exception {
    return new MongoProperties();
  }

  @Bean(name = "hcServerProperties")
  @ConfigurationProperties(prefix = "spring.data.mongodb.hcserver")
  public MongoProperties getHCServerProps() throws Exception {
    return new MongoProperties();
  }

  @Primary
  @Bean(name = "serverMongoTemplate")
  public MongoTemplate serverMongoTemplate() throws Exception {
    return new MongoTemplate(serverMongoDatabaseFactory(getServerProps()));
  }

  @Bean(name ="hcServerMongoTemplate")
  public MongoTemplate hcServerMongoTemplate() throws Exception {
    return new MongoTemplate(hcServerMongoDatabaseFactory(getHCServerProps()));
  }

  @Primary
  @Bean
  public MongoDatabaseFactory serverMongoDatabaseFactory(MongoProperties mongo) throws Exception {
    return new SimpleMongoClientDatabaseFactory(
      mongo.getUri()
    );
  }

  @Bean
  public MongoDatabaseFactory hcServerMongoDatabaseFactory(MongoProperties mongo) throws Exception {
    return new SimpleMongoClientDatabaseFactory(mongo.getUri());
  }
}


