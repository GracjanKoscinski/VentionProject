package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.service.password.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reset password controller responsible for resetting user passwords.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Reset password Controller")
@RequestMapping("/auth/password")
public class ResetPasswordController {

  private final PasswordService resetPasswordService;

  /**
   * Reset the user's password.
   */
  @Operation(summary = "Reset password")
  @PostMapping("/reset")
  public ResponseEntity<StandardizedResponse<String>> resetPassword(
      @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
    resetPasswordService.resetPassword(resetPasswordRequest);
    return ResponseEntity.status(HttpStatus.OK).body(StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "New password has been set"));
  }
}
