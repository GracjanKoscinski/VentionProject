package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for returning medical test information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with medical test information")
public class MedicalTestResponse {
  
  @Schema(description = "Test id", example = "1")
  private Long id;

  @Schema(description = "Name of the medical test", example = "Allergy Skin Test")
  private String test;

  @Schema(description = "Medical test duration", example = "30")
  private Long duration;

  @Schema(description = "Location information")
  private List<LocationResponse> locations;
}
