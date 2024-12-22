package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the root of application.yml.
 */
@Validated
@ConfigurationProperties
public record AppConfig(
    @NotBlank(message = "baseUrl must not be blank")
    String baseUrl,
    @PositiveOrZero(message = "temporaryPasswordValidityPeriod must not be negative")
    int temporaryPasswordValidityDays,

    @PositiveOrZero(message = "elementCountLimit must not be negative")
    int elementCountLimit
) {

}
