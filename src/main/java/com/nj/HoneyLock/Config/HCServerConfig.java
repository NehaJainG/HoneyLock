package com.nj.HoneyLock.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.nj.HoneyLock.hcServer.repo"},
  mongoTemplateRef = HCServerConfig.MONGO_TEMPLATE
)
public class HCServerConfig {
  protected static final String MONGO_TEMPLATE = "hcServerMongoTemplate";
}
