package com.ventionteams.medfast.dto.request.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update address info request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Update address info request")
public class AddressInfoRequest {

  @Schema(description = "Street address", example = "123 Main Street")
  @Size(
      min = 2,
      max = 50,
      message = "Street address's length must not be less than 2 or greater than 50 characters"
  )
  @NotBlank(message = "Street address must not be blank")
  private String streetAddress;

  @Schema(description = "House", example = "42 a")
  @Size(
      min = 1,
      max = 20,
      message = "House's length must not be less than 1 or greater than 20 characters"
  )
  @NotBlank(message = "House must not be blank")
  private String house;

  @Schema(description = "Apartment", example = "10")
  @Size(
      min = 1,
      max = 20,
      message = "Apartment's length must not be less than 1 or greater than 20 characters"
  )
  @NotBlank(message = "Apartment must not be blank")
  private String apartment;

  @Schema(description = "City", example = "Chicago")
  @Size(
      min = 1,
      max = 50,
      message = "City's length must not be less than 1 or greater than 50 characters"
  )
  @NotBlank(message = "City must not be blank")
  private String city;

  @Schema(description = "State", example = "Illinois")
  @Size(
      min = 2,
      max = 50,
      message = "State's length must not be less than 1 or greater than 50 characters"
  )
  @NotBlank(message = "State must not be blank")
  private String state;

  @Schema(description = "ZIP", example = "60007")
  @Size(min = 5, max = 5, message = "ZIP's length must be 5 characters")
  @NotBlank(message = "ZIP must not be blank")
  private String zip;

}