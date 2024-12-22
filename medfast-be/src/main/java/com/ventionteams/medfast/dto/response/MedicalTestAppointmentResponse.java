package com.ventionteams.medfast.dto.response;

import com.ventionteams.medfast.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with a medical test.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a medical test")
public class MedicalTestAppointmentResponse {

  @Schema(description = "Medical test id", example = "3")
  private long id;

  @Schema(description = "Test name", example = "Blood test")
  private String testName;

  @Schema(description = "status", example = "SHEDULED")
  private AppointmentStatus status;

  @Schema(description = "DateTime of test")
  private LocalDateTime dateTimeFrom;

  @Schema(description = "DateTime of test")
  private LocalDateTime dateTimeTo;

  @Schema(description = "location")
  private String location;

  @Schema(description = "Tells if test appointment has pdf result", example = "true")
  private Boolean hasPdfResult;
}
