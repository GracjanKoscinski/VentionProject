package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests user activation/deactivation functionality in admin controller with integration tests.
 */
@RequiredArgsConstructor
public class UserActivationTests extends IntegrationTest {

  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<Person> personProvider;
  private String adminToken;
  private User doctorUser;

  /**
   * Prepares an admin access token.
   */
  @BeforeAll
  public void setup() {
    HospitalService service = hospitalServiceProvider.provide();
    Specialization specialization = specializationProvider.provide(List.of(service));
    Location location = locationProvider.provide();
    Doctor doctor = doctorProvider.provide(List.of(location, List.of(specialization)));
    doctorUser = userProvider.provide(List.of(doctor));
    User admin = userProvider.provide(List.of(personProvider.provide()));
    Map<String, String> request = new HashMap<>();
    request.put("email", admin.getEmail());
    request.put("password", ((UserProvider) userProvider).getRawPassword(admin.getEmail()));

    adminToken = "Bearer " + given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin")
        .then()
        .statusCode(200)
        .extract()
        .path("data.accessToken");
  }

  @Test
  public void deactivateUser_ValidRequest() {

    given()
        .contentType(ContentType.JSON)
        .param("user-email", doctorUser.getEmail())
        .header("Authorization", adminToken)
        .when()
        .put("api/admin-console/deactivate")
        .then()
        .statusCode(200)
        .body("message", equalTo("Operation successful"))
        .body("data", nullValue());
  }

  @Test
  public void activateUser_ValidRequest() {

    given()
        .contentType(ContentType.JSON)
        .param("user-email", doctorUser.getEmail())
        .header("Authorization", adminToken)
        .when()
        .put("api/admin-console/activate")
        .then()
        .statusCode(200)
        .body("message", equalTo("Operation successful"))
        .body("data", nullValue());
  }
}

