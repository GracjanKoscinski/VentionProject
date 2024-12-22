package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.service.password.OneTimePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * One time password controller responsible for provisioning and verifying one time passwords.
 */
@Log4j2
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "One-time password Controller")
@RequestMapping("/auth/otp")
public class OneTimePasswordController {

  private final OneTimePasswordService oneTimePasswordService;

  /**
   * Send a one time password to the user's email.
   */
  @Operation(summary = "Request one time password")
  @PostMapping
  public ResponseEntity<StandardizedResponse<String>> sendOtp(
      @Email @RequestParam("email") String email) throws MessagingException {

    StandardizedResponse<String> response;
    oneTimePasswordService.sendResetPasswordEmail(email);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Reset password email has been sent");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Verify the one time password sent from user.
   */
  @Operation(summary = "Verify one time password")
  @PostMapping("/verify")
  public ResponseEntity<StandardizedResponse<String>> verifyOtp(
      @Email @RequestParam("email") String email,
      @Size(min = 4, max = 4) @RequestParam("token") String token) {

    StandardizedResponse<String> response;
    oneTimePasswordService.verify(email, token);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Token has been verified");

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
