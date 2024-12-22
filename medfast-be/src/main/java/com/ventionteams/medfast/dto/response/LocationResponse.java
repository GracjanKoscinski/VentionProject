package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for returning location information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response with location information")
public class LocationResponse {

  @Schema(description = "Location id", example = "1")
    private Long id;

  @Schema(description = "Name of the hospital or location", example = "Central Hospital")
    private String hospitalName;

  @Schema(description = "Street address of the location", example = "456 Elm Street")
    private String streetAddress;

  @Schema(description = "House number of the location", example = "A-12")
    private String house;
}

