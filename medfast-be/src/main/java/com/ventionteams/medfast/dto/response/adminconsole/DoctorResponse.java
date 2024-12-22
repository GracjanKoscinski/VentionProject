package com.ventionteams.medfast.dto.response.adminconsole;

import com.ventionteams.medfast.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response with doctor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with doctor")
public class DoctorResponse {

  @Schema(description = "Name", example = "Alex Smith")
  private String name;

  @Schema(description = "Email", example = "johndoe@gmail.com")
  private String email;

  @Schema(description = "List of specializations", example = "Allergist")
  private List<String> specializations;

  @Schema(description = "Status set by admin", example = "ACTIVE")
  private UserStatus status;
}
