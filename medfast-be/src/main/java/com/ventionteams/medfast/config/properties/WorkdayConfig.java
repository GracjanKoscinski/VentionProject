package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the workday descendants of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "workday")
public record WorkdayConfig(
    @NotNull(message = "workday.startTime must not be blank")
    LocalTime startTime,
    @NotNull(message = "workday.endtime must not be blank")
    LocalTime endTime
) {

}
