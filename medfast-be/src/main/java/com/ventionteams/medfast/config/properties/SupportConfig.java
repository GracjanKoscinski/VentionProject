package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the spring descendants of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "support")
public record SupportConfig(
    @NotBlank(message = "support.phoneNumber must not be null")
    String phoneNumber
){}
