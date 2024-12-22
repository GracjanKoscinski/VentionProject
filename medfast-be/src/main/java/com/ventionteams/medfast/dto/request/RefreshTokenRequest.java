package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Access token refresh token request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Refresh token request")
public class RefreshTokenRequest {

  @Schema(description = "Refresh token", example = "550e8400-e29b-41d4-a716-446655440000")
  @Size(min = 36, max = 36, message = "Refresh token must contain UUID of 36 characters")
  @NotBlank(message = "Refresh token must not be blank")
  private String refreshToken;
}
