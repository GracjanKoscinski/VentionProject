package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reset password request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Reset password request")
public class ResetPasswordRequest {

  @Schema(description = "One time password", example = "4325")
  @Size(min = 4, max = 4, message = "One time password must contain 4 characters")
  private String otp;

  @Schema(description = "New password", example = "password")
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
  private String newPassword;

  @Schema(description = "Email", example = "user@example.com")
  @Email(message = "Email must follow the format user@example.com")
  private String email;
}
