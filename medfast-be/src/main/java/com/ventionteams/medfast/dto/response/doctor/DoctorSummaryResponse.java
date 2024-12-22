package com.ventionteams.medfast.dto.response.doctor;

import com.ventionteams.medfast.dto.response.userinfo.UserPhotoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response with a doctor.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a doctor")
public class DoctorSummaryResponse {

  @Schema(description = "Doctor’s id", example = "John Doe")
  private Long id;

  @Schema(description = "Doctor’s name", example = "John Doe")
  private String fullName;

  @Schema(description = "Doctor’s specializations",
      example = "[\"Cardiologist\",\n\"Cardio-rheumatologist\"]")
  private List<String> specializations;

  @Schema(description = "Photo of a doctor")
  private UserPhotoResponse userPhotoResponse;
}
