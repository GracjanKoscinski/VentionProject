package com.ventionteams.medfast.dto.response.userinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with user info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with user info")
public class UserInfoResponse {

  @Schema(description = "Has checked terms and conditions", example = "false")
  private boolean checkboxTermsAndConditions;

  @Schema(description = "Personal info")
  private PersonalInfoResponse personalInfo;

  @Schema(description = "Address info")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AddressInfoResponse addressInfo;

  @Schema(description = "Contact info")
  private ContactInfoResponse contactInfo;

}
