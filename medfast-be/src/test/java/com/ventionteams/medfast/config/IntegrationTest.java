package com.ventionteams.medfast.config;

import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests. Sets up the RestAssured configuration.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(PostgreContainerExtension.class)
public abstract class IntegrationTest {

  @LocalServerPort
  public int port;

  @PostConstruct
  public void initRestAssured() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  /**
   * Cleans the database before all tests in the test class.
   */
  @BeforeAll
  public void cleanDatabaseBeforeTests() throws Exception {
    cleanDatabase();
  }

  /**
   * Cleans the database after all tests in the test class.
   */
  @AfterAll
  public void cleanDatabaseAfterTests() throws Exception {
    cleanDatabase();
  }

  /**
   * Utility method to perform database cleanup.
   */
  private void cleanDatabase() throws Exception {
    PostgreSQLContainer<?> container = PostgreContainerExtension.getContainer();

    try (Connection connection = container.createConnection("")) {
      Statement statement = connection.createStatement();

      statement.execute("""
          DO $$ DECLARE r RECORD;
          BEGIN
              FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public'
              AND tablename NOT IN ('appointment_types', 'genders', 'roles', 'appointment_statuses',
               'databasechangelog', 'databasechangeloglock', 'user_statuses')) LOOP
                  EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' CASCADE';
              END LOOP;
          END $$;""");

      statement.close();
    }
  }
}
