package com.ventionteams.medfast.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for tests. Adds the faker bean for generating fake data in entity provider
 * classes.
 */
@Configuration
public class TestConfiguration {

  @Bean
  public Faker faker() {
    return new Faker();
  }
}
