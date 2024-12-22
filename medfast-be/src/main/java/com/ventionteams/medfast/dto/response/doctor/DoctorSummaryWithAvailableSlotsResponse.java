package com.ventionteams.medfast.dto.response.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response with a doctor.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response with a doctor")
public class DoctorSummaryWithAvailableSlotsResponse extends DoctorSummaryResponse {

  @Schema(description = "Available slots amount", example = "2")
  private int availableSlots;
}
