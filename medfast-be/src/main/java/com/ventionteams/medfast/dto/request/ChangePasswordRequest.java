package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Change password request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Change password request")
public class ChangePasswordRequest {

  @Schema(description = "Current password", example = "qweRTY123$")
  @NotBlank
  private String currentPassword;

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
}
