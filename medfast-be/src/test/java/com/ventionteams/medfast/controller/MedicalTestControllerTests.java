package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the medical test controller functionality with integration tests.
 */
@ExtendWith(PostgreContainerExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class MedicalTestControllerTests extends IntegrationTest {
  
  @Autowired
  private EntityProvider<User> userProvider;
  @Autowired
  private EntityProvider<Patient> patientProvider;
  @Autowired
  private EntityProvider<Location> locationProvider;
  @Autowired
  private EntityProvider<MedicalTest> medicalTestProvider;
  private String jwtPatient;
  private String keyword;
  private MedicalTest medicalTest;

  /**
   * Sets up entities before test.
   */
  @BeforeAll
  public void setUp() {
    Patient patient = patientProvider.provide();
    User userPatient = userProvider.provide(List.of(patient));
    SignInRequest request = new SignInRequest(userPatient.getEmail(),
        ((UserProvider) userProvider).getRawPassword(userPatient.getEmail()));
    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");
    jwtPatient = response.body().jsonPath().getString("data.accessToken");
    Location location = locationProvider.provide();
    medicalTest = medicalTestProvider.provide(List.of(location));
    keyword = medicalTest.getTest().substring(3);
  }

  @Test
  public void findTestsByKeyword_ValidRequest_ReturnsOk() {
    given()
        .contentType(ContentType.JSON)
        .param("keyword", keyword)
        .header("Authorization", "Bearer " + jwtPatient)
        .when()
        .get("/api/patient/tests")
        .then()
        .statusCode(200)
        .body("data[0].id", notNullValue())
        .body("data[0].test", equalTo(medicalTest.getTest()));
  }
}
