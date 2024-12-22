package com.ventionteams.medfast.dto.response;

import com.ventionteams.medfast.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Sign in response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Sign in response")
public class SignInResponse extends JwtAuthenticationResponse {

  @Schema(description = "Role of signed in user", example = "DOCTOR")
  private Role role;
}
