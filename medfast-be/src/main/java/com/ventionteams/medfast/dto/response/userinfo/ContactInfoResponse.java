package com.ventionteams.medfast.dto.response.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with user contact info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with user contact info")
public class ContactInfoResponse {

  @Schema(description = "Phone number", example = "15551234567")
  private String phone;

  @Schema(description = "Email", example = "johndoe@gmail.com")
  private String email;

}
