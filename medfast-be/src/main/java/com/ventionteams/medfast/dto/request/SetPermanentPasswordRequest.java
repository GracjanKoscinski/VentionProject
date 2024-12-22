package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Set permanent password request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Set permanent password request")
public class SetPermanentPasswordRequest implements EmailRequest {

  @Schema(description = "4 digit code", example = "1589")
  @NotBlank
  private String code;

  @Schema(description = "New password", example = "qwerty123ASD!")
  @Size(min = 10, max = 50,
      message = "Password's length must not be less than 10 or greater than 50 character")
  @NotBlank
  @Pattern(
      regexp =
          "^(?=.*[0-9])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~])"
              + "(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{10,50}$",
      message =
          "Password must contain at least one digit, one special character,"
              + " one lowercase, and one uppercase letter, and no whitespace"
  )
  private String newPassword;

  @Schema(description = "Email", example = "user@example.com")
  @Email(message = "Email must follow the format user@example.com")
  private String email;

  @Schema(description = "TermsAndConditions", example = "true")
  @NotNull(message = "Terms and conditions must not be null")
  private Boolean checkboxTermsAndConditions;
}
