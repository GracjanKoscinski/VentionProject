package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.request.SetPermanentPasswordRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.password.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Password controller responsible for changing user passwords.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name = "Password actions Controller")
@RequestMapping("/api")
public class PasswordController {

  private final PasswordService passwordService;

  /**
   * Change the user's password.
   */
  @Operation(summary = "Change password")
  @PostMapping("/patient/settings/password/change")
  public ResponseEntity<StandardizedResponse<String>> changePassword(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

    StandardizedResponse<String> response;

    passwordService.changePassword(authenticatedUser, changePasswordRequest);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Your password is changed");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Set permanent password.
   */
  @Operation(summary = "Set permanent password")
  @PutMapping("/doctor/settings/password/setPermanentPassword")
  public ResponseEntity<StandardizedResponse<Void>> setPermanentPassword(
      @RequestBody @Valid SetPermanentPasswordRequest setPermanentPasswordRequest) {

    passwordService.setPermanentPassword(setPermanentPasswordRequest);
    StandardizedResponse<Void> response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Your temporary password is changed on a permanent one");

    return ResponseEntity.status(response.getStatus()).body(response);

  }
}
