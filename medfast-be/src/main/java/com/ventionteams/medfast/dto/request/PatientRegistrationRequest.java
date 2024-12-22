package com.ventionteams.medfast.dto.request;

import com.ventionteams.medfast.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Patient sign up request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Patient's sign up request")
public class PatientRegistrationRequest extends SignUpRequest {

  @Schema(description = "Password", example = "12312312")
  @Size(
      min = 10,
      max = 50,
      message = "Password's length must not be less than 10 or greater than 50 characters"
  )
  @NotBlank(message = "Password must not be blank")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\"
          + "[\\]^_`{|}~])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{10,50}$",
      message = "Password must contain at least one digit, one special character,"
          + " one lowercase, and one uppercase letter, and no whitespace"
  )
  private String password;

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


  @Schema(description = "Legal sex", example = "MALE")
  @NotNull(message = "Legal sex must not be blank")
  private Gender sex;

  @Schema(description = "Citizenship", example = "Canada")
  @Size(
      min = 2,
      max = 50,
      message = "Citizenship's length must not be less than 2 or greater than 50 characters"
  )
  @NotBlank(message = "Citizenship must not be blank")
  private String citizenship;

  @Schema(description = "TermsAndConditions", example = "true")
  @NotNull(message = "Terms and conditions must not be null")
  private Boolean checkboxTermsAndConditions;

}
