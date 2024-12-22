package com.ventionteams.medfast.config.extension;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Adds a persistent postgres container for use in tests. Only started if running a test that
 * references it and then only shut down when the jvm is shut down, so it can be reused in multiple
 * tests
 */
public class PostgreContainerExtension implements Extension {

  // DEVNOTE: We do not wrap it with try-with-resources based on
  // the testcontainers doc: https://java.testcontainers.org/features/reuse/
  @Container
  static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:16")
      .withReuse(false);

  static {
    CONTAINER.start();
    System.setProperty("DB_URL", CONTAINER.getJdbcUrl());
    System.setProperty("DB_USERNAME", CONTAINER.getUsername());
    System.setProperty("DB_PASSWORD", CONTAINER.getPassword());
  }

  public static PostgreSQLContainer<?> getContainer() {
    return CONTAINER;
  }
}
