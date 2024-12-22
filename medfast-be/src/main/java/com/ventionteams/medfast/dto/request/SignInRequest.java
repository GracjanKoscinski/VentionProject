package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Sign in request transfer object.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Sign in request")
public class SignInRequest implements EmailRequest {

  @Schema(description = "Email", example = "johndoe@gmail.com")
  @Size(min = 10, max = 50, message = "Email must contain from 10 to 50 characters")
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must follow the format user@example.com")
  private String email;

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
}
