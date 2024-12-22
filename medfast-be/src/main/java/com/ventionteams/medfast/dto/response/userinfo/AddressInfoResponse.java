package com.ventionteams.medfast.dto.response.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response with user address info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with user address info")
public class AddressInfoResponse {

  @Schema(description = "Street address", example = "123 Main Street")
  private String streetAddress;

  @Schema(description = "House", example = "42 a")
  private String house;

  @Schema(description = "Apartment", example = "10")
  private String apartment;

  @Schema(description = "City", example = "Chicago")
  private String city;

  @Schema(description = "State", example = "Illinois")
  private String state;

  @Schema(description = "ZIP", example = "60007")
  private String zip;
}
