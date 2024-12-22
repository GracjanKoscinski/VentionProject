package com.ventionteams.medfast.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Doctor registration request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DoctorRegistrationRequest extends SignUpRequest {

  @NotBlank
  @Pattern(regexp = "^\\d{7}$", message = "License number must be in the format XXXXXXX")
  private String licenseNumber;

  @NotNull(message = "Location ID cannot be null")
  private Long locationId;

  @NotNull(message = "Specializations cannot be null")
  private List<Long> specializationIds;

}
