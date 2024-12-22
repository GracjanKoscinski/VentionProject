package com.ventionteams.medfast.dto.response;

import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with a consultation appointment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a consultation appointment")
public class AppointmentResponse {

  @Schema(description = "Consultation appointment id", example = "10")
  private long id;

  @Schema(description = "Doctor's summary")
  private DoctorSummaryResponse doctorSummary;

  @Schema(description = "Date and time from in 'yyyy-MM-dd HH:mm:ss' format",
      example = "2021-05-03 18:30:00")
  private String dateFrom;

  @Schema(description = "Date and time to in 'yyyy-MM-dd HH:mm:ss' format",
      example = "2021-05-03 18:45:00")
  private String dateTo;

  @Schema(description = "Location address", example = "John Hopkins Hospital, 1800 Orleans St")
  private String location;

  @Schema(description = "Status", example = "Scheduled (Confirmed)")
  private String status;

  @Schema(description = "Type (online/on-site)", example = "on-site")
  private String type;
}
