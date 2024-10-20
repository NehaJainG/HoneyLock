package com.nj.HoneyLock.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.nj.HoneyLock.server.repo"},
  mongoTemplateRef = ServerConfig.MONGO_TEMPLATE
)
public class ServerConfig {
  protected static final String MONGO_TEMPLATE = "serverMongoTemplate";
}
