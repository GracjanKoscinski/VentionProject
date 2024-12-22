package com.ventionteams.medfast.config;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class. Provides general utility beans.
 */
@Configuration
public class AppConfig {

  @Bean
  public Random random() {
    return new Random();
  }
}
