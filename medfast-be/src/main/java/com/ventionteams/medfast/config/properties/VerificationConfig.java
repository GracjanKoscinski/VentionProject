package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the verification descendants of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "verification")
public record VerificationConfig(
    @NotNull(message = "verification.code must not be null")
    Code code
) {

  /**
   * Configuration properties for the code descendants.
   */
  public record Code(
      @NotNull(message = "verification.code.timeout must not be null")
      @PositiveOrZero(message = "verification.code.timeout must be greater or equal to 0")
      int timeout
  ) {

  }
}
