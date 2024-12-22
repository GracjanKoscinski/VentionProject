package com.ventionteams.medfast.dto.response.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with profile picture in bytes an content type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with user profile picture")
public class UserPhotoResponse {

  private byte[] userPhoto;
  private String contentType;
}
