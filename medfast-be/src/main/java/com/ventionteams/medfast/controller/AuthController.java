package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.service.auth.AuthenticationService;
import com.ventionteams.medfast.service.auth.RefreshTokenService;
import com.ventionteams.medfast.service.auth.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller that handles sign up, sign in, refresh token, verify email and
 * reverification of users.
 */
@Log4j2
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Sign in Controller")
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationService authenticationService;
  private final RefreshTokenService refreshTokenService;
  private final VerificationTokenService verificationTokenService;

  /**
   * Sign up a user.
   */
  @Operation(summary = "Sign up")
  @PostMapping("/signup")
  public ResponseEntity<StandardizedResponse<String>> signUp(
      @RequestBody @Valid PatientRegistrationRequest request)
      throws MessagingException {
    StandardizedResponse<String> response;

    String signupResponse = authenticationService.signUp(request);
    response = StandardizedResponse.ok(
        signupResponse,
        HttpStatus.OK.value(),
        "Sign up successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Sign in a user.
   */
  @Operation(summary = "Sign in")
  @PostMapping("/signin")
  public ResponseEntity<StandardizedResponse<JwtAuthenticationResponse>> signIn(
      @RequestBody @Valid SignInRequest request) {
    StandardizedResponse<JwtAuthenticationResponse> response;

    JwtAuthenticationResponse authenticationResponse = authenticationService.signIn(request);
    response = StandardizedResponse.ok(
        authenticationResponse,
        HttpStatus.OK.value(),
        "Sign in successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Refresh access token.
   */
  @Operation(summary = "Refresh access token")
  @PostMapping("/refresh")
  public ResponseEntity<StandardizedResponse<JwtAuthenticationResponse>> refreshToken(
      @RequestBody @Valid RefreshTokenRequest request) {
    StandardizedResponse<JwtAuthenticationResponse> response;

    JwtAuthenticationResponse refreshResponse = refreshTokenService.refreshToken(request);
    response = StandardizedResponse.ok(
        refreshResponse,
        HttpStatus.OK.value(),
        "Refreshing token successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Verify email address.
   */
  @Operation(summary = "Verify email address")
  @PostMapping("/verify")
  public ResponseEntity<StandardizedResponse<String>> verifyUser(
      @Email @RequestParam("email") String email, @RequestParam("code") String code) {
    StandardizedResponse<String> response;

    verificationTokenService.verify(email, code);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Your account is verified");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Request another verification email.
   */
  @Operation(summary = "Request another verification email")
  @PostMapping("/reverify")
  public ResponseEntity<StandardizedResponse<String>> reverifyUser(
      @Email @RequestParam("email") String email) throws MessagingException {
    StandardizedResponse<String> response;

    authenticationService.sendVerificationEmail(email);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Another email has been sent to your email");

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
