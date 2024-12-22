package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.EmailService;
import jakarta.mail.MessagingException;
import java.util.List;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * Tests the one time password controller functionality with integration tests.
 */
@RequiredArgsConstructor
class OneTimePasswordControllerTests extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final OneTimePasswordRepository oneTimePasswordRepository;

  @SpyBean
  private EmailService emailService;

  @Test
  void sendOtpAndVerifyIt_ValidEmailAndOtp_SendsOtpAndReturnsOkOnVerify()
      throws MessagingException {
    User patient = userProvider.provide(List.of(patientProvider.provide()));

    // DEVNOTE: We mock the email service to avoid sending actual emails.
    doNothing().when(emailService).sendResetPasswordEmail(any(User.class), any(String.class));

    given()
        .param("email", patient.getEmail())
        .when()
        .post("/auth/otp")
        .then()
        .statusCode(200)
        .body("message", equalTo("Reset password email has been sent"));

    OneTimePassword oneTimePasswords = oneTimePasswordRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new AssertionFailedError("No otp was created for the user."));

    given()
        .param("email", patient.getEmail())
        .param("token", oneTimePasswords.getToken())
        .when()
        .post("/auth/otp/verify")
        .then()
        .statusCode(200)
        .body("message", equalTo("Token has been verified"));
  }
}
