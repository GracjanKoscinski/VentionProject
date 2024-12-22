package com.ventionteams.medfast.dto.response.adminconsole;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with doctor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with doctor")
public class DoctorListResponse {

  @Schema(description = "List of doctors")
  private List<DoctorResponse> doctors;
  @Schema(description = "Total amount of doctors", example = "10")
  private int totalAmount;
}
