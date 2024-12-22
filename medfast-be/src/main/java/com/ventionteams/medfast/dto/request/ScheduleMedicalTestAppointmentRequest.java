package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create medical appointment test request transfer object.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create test request")
public class ScheduleMedicalTestAppointmentRequest {

  @Schema(description = "Time of test in yyyy-MM-dd HH:mm:ss")
  @NotNull(message = "Date of test must not be null")
  private ZonedDateTime dateTime;

  @Schema(description = "Test id")
  @NotNull(message = "Test id must not be null")
  private Long testId;

  @Schema(description = "Location id")
  @NotNull(message = "Location id must not be null")
  private Long locationId;
}
