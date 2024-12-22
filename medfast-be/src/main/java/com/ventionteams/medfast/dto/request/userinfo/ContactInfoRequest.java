package com.ventionteams.medfast.dto.request.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update contact info request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Update contact info request")
public class ContactInfoRequest {

  @Schema(description = "Email", example = "johndoe@gmail.com")
  @Size(min = 10, max = 50, message = "Email must contain from 10 to 50 characters")
  @Email(message = "Email must follow the format user@example.com")
  @NotBlank(message = "Email must not be blank")
  private String email;

  @Schema(description = "Phone number", example = "15551234567")
  @Size(
      min = 11,
      max = 11,
      message = "Phone number's length must be 11 characters"
  )
  @NotBlank(message = "Phone number must not be blank")
  private String phone;

}