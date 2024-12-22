package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the root of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "google")
public record GoogleConfig(
    @NotNull(message = "Google Meet link must not be null")
    Meet meet
) {

  /**
   * Configuration properties for the Google Meet link.
   */
  public record Meet(
      @NotBlank(message = "Google Meet link must not be blank")
      String link
  ) {
    @Override
      public String toString() {
      return link;
    }
  }

  public String getGoogleMeetLink() {
    return meet.link();
  }
}
