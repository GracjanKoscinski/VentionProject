package com.ventionteams.medfast.dto.request.userinfo;

import com.ventionteams.medfast.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update personal info request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Update personal info request")
public class PersonalInfoRequest {

  @Schema(description = "Legal sex", example = "MALE")
  @NotNull(message = "Sex must not be blank")
  private Gender sex;

  @Schema(description = "Citizenship", example = "Canada")
  @Size(
      min = 2,
      max = 50,
      message = "Citizenship's length must not be less than 2 or greater than 50 characters"
  )
  @NotBlank(message = "Citizenship must not be blank")
  private String citizenship;

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

  @NotNull(message = "Birth date must not be blank")
  @Schema(description = "Date of birth in yyyy-mm-dd", example = "2000-07-09")
  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

}