package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.UserRepository;
import io.restassured.http.ContentType;
import java.util.List;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests the reset password controller functionality with integration tests.
 */
@RequiredArgsConstructor
class ResetPasswordControllerTests extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<OneTimePassword> oneTimePasswordEntityProvider;
  private final Faker faker;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  @Test
  void resetPassword_ValidRequest_ResetsPasswordAndReturnsOk() {
    User user = userProvider.provide(List.of(patientProvider.provide()));
    OneTimePassword otp = oneTimePasswordEntityProvider.provide(List.of(user));
    String newPassword = faker.internet().password(10, 50,
        true, true, true);

    given()
        .contentType(ContentType.JSON)
        .body(new ResetPasswordRequest(otp.getToken(), newPassword, user.getEmail()))
        .when()
        .post("/auth/password/reset")
        .then()
        .statusCode(200)
        .body("message", equalTo("New password has been set"));

    User userWithResetPassword = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new AssertionFailedError("No user was found by the email was found."));

    assertTrue(passwordEncoder.matches(newPassword, userWithResetPassword.getPassword()),
        "User password has not been reset in the database.");
  }
}
