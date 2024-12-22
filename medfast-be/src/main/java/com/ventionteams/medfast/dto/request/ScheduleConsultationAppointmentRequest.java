package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Schedule consultation appointment request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Schedule consultation appointment request")
public class ScheduleConsultationAppointmentRequest {

  @Schema(description = "Service id", example = "23")
  @NotNull
  private Long serviceId;

  @Schema(description = "Doctor id", example = "23")
  @NotNull
  private Long doctorId;

  @Schema(description = "Location id, (optional, can be null)", example = "23")
  private Long locationId;

  @Schema(description = "Date and time from in 'yyyy-MM-dd HH:mm:ss' format",
      example = "2021-05-03T18:30:00+01:00")
  @NotNull
  private ZonedDateTime dateFrom;
}
