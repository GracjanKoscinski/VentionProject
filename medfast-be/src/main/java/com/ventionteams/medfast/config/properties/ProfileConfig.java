package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the root of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "profile")
public record ProfileConfig(
    @NotBlank(message = "defaultProfilePicturePath must not be blank")
    String defaultProfilePicturePath
) {

}
