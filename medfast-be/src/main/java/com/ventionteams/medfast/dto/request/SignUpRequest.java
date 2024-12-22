package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Sign up request transfer object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Sign up request")
public class SignUpRequest implements EmailRequest {

  @Schema(description = "Email", example = "johndoe@gmail.com")
  @Size(min = 10, max = 50, message = "Email must contain from 10 to 50 characters")
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must follow the format user@example.com")
  private String email;

  @Schema(description = "Name", example = "Alex")
  @Size(
      min = 2,
      max = 50,
      message = "Name's length must not be less than 2 or greater than 50 characters"
  )
  @NotBlank(message = "Name must not be blank")
  private String name;

  @Schema(description = "Surname", example = "Smith")
  @Size(
      min = 2,
      max = 50,
      message = "Surname's length must not be less than 2 or greater than 50 characters"
  )
  @NotBlank(message = "Surname must not be blank")
  private String surname;

  @Schema(description = "Date of birth in yyyy-mm-dd", example = "2000-07-09")
  @Past(message = "Birth date must be in the past")
  @NotNull(message = "Birth date must not be blank")
  private LocalDate birthDate;

  @Schema(description = "Phone number", example = "15551234567")
  @Size(
      min = 11,
      max = 11,
      message = "Phone number's length must be 11 characters"
  )
  @NotBlank(message = "Phone number must not be blank")
  private String phone;

}
