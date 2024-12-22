package com.ventionteams.medfast.dto.response.userinfo;

import com.ventionteams.medfast.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with user personal info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with user personal info")
public class PersonalInfoResponse {

  @Schema(description = "Name", example = "Alex")
  private String name;

  @Schema(description = "Surname", example = "Smith")
  private String surname;

  @Schema(description = "Date of birth in yyyy-mm-dd", example = "2000-07-09")
  private LocalDate birthDate;

  @Schema(description = "Legal sex", example = "MALE")
  private Gender sex;

  @Schema(description = "Citizenship", example = "Canada")
  private String citizenship;

}
