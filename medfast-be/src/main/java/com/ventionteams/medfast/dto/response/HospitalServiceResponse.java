package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with hospital service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response with an hospital service")
public class HospitalServiceResponse {

  @Schema(description = "Service id", example = "1")
  private Long id;

  @Schema(description = "Service name", example = "Hysterectomy")
  private String service;

  @Schema(description = "Service duration in minutes", example = "60")
  private Long duration;
}
