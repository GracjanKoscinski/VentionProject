package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.request.SetPermanentPasswordRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.UserRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the password controller functionality with integration tests.
 */
@RequiredArgsConstructor
class PasswordControllerTests extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<OneTimePassword> oneTimePasswordProvider;
  private final UserRepository userRepository;
  private final EntityProvider<HospitalService> hospitalServiceProvider;

  private String patientJwt;
  private String doctorJwt;
  private User patientUser;
  private User doctorUser;
  private OneTimePassword otp;

  /**
   * Setups and integrates the necessary entities into db for use in further tests.
   */
  @BeforeAll
  void setUp() {
    Patient patient = patientProvider.provide();
    HospitalService hospitalService = hospitalServiceProvider.provide();
    Specialization specialization = specializationProvider.provide(List.of(hospitalService));
    Location location = locationProvider.provide();
    Doctor doctor = doctorProvider.provide(List.of(location, List.of(specialization)));
    patientUser = userProvider.provide(List.of(patient));
    doctorUser = userProvider.provide(List.of(doctor));
    otp = oneTimePasswordProvider.provide(List.of(doctorUser));

    SignInRequest request = new SignInRequest(patientUser.getEmail(),
        ((UserProvider) userProvider).getRawPassword(patientUser.getEmail()));
    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");
    patientJwt = response.body().jsonPath().getString("data.accessToken");

    request = new SignInRequest(doctorUser.getEmail(),
        ((UserProvider) userProvider).getRawPassword(doctorUser.getEmail()));
    response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");
    doctorJwt = response.body().jsonPath().getString("data.accessToken");
  }

  @Test
  void changePassword_ValidRequest_ChangesPasswordAndReturnsOk() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword(
        ((UserProvider) userProvider).getRawPassword(patientUser.getEmail()));
    request.setNewPassword("qwerty123ASD!");

    String passwordBefore = patientUser.getPassword();

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + patientJwt)
        .body(request)
        .when()
        .post("/api/patient/settings/password/change")
        .then()
        .statusCode(200)
        .body("message", equalTo("Your password is changed"));

    User userAfter = userRepository.findByEmail(patientUser.getEmail())
        .orElseThrow(
            () -> new AssertionFailedError(
                "No user with this email in db: " + patientUser.getEmail()));
    if (passwordBefore.equals(userAfter.getPassword())) {
      throw new AssertionFailedError("Password was not changed");
    }
  }

  @Test
  void setPermanentPassword_ValidRequest_CreatesPasswordAndReturnsOk() {
    SetPermanentPasswordRequest request = createRequest(
        "qweRTY123$", otp.getToken(), doctorUser.getEmail(), true);

    String passwordBefore = doctorUser.getPassword();

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + doctorJwt)
        .body(request)
        .when()
        .put("/api/doctor/settings/password/setPermanentPassword")
        .then()
        .statusCode(200)
        .body("message", equalTo("Your temporary password is changed on a permanent one"));

    User userAfter = userRepository.findByEmail(doctorUser.getEmail())
        .orElseThrow(
            () -> new AssertionFailedError(
                "No user with this email in db: " + doctorUser.getEmail()));
    if (passwordBefore.equals(userAfter.getPassword())) {
      throw new AssertionFailedError("Temporary password was not changed");
    }
    if (!userAfter.isCheckboxTermsAndConditions()) {
      throw new AssertionFailedError("The terms and conditions checkbox has not been checked");
    }
  }

  private SetPermanentPasswordRequest createRequest(
      String newPassword,
      String code,
      String email,
      Boolean termsAndConditions
  ) {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setNewPassword(newPassword);
    request.setCode(code);
    request.setEmail(email);
    request.setCheckboxTermsAndConditions(termsAndConditions);
    return request;
  }
}
