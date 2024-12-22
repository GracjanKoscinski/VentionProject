package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reschedule medical test appointment request transfer object.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Reschedule medical test appointment request")
public class RescheduleMedicalTestAppointmentRequest {

  @Schema(description = "Consultation appointment id", example = "23")
  @NotNull
  private Long appointmentId;

  @Schema(description = "Location id, (optional, can be null)", example = "23")
  private Long locationId;

  @Schema(description = "Date and time from in 'yyyy-MM-dd HH:mm:ss' format",
      example = "2021-05-03T18:30:00+01:00")
  @NotNull
  private ZonedDateTime dateFrom;
}
