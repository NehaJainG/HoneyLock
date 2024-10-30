package com.nj.HoneyLock.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**") // Adjust this to match your API endpoints
      .allowedOrigins("http://localhost:3000") // Specify frontend origin
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowCredentials(true) // Enable credentials
      .allowedHeaders("*");
  }
}
