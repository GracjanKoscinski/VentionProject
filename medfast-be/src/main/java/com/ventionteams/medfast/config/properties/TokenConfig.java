package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the token descendants of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "token")
public record TokenConfig(
    @NotNull(message = "token.timeout must not be null")
    Timeout timeout,
    @NotNull(message = "token.signing must not be null")
    Signing signing
) {

  /**
   * Configuration properties for the timeout descendants.
   */
  public record Timeout(
      @NotNull(message = "token.timeout.access must not be null")
      @PositiveOrZero(message = "token.timeout.access must be greater or equal to 0")
      long access,
      @NotNull(message = "token.timeout.refresh must not be null")
      @PositiveOrZero(message = "token.timeout.refresh must be greater or equal to 0")
      long refresh,
      @NotNull(message = "token.timeout.password-reset must not be null")
      @PositiveOrZero(message = "token.timeout.refresh must be greater or equal to 0")
      long resetPassword
  ) {

  }

  /**
   * Configuration properties for the signing descendants.
   */
  public record Signing(
      @NotBlank(message = "token.signing.key must not be blank")
      @Size(min = 50, message = "token.signing.key's length must be greater than 50")
      String key
  ) {

  }
}

