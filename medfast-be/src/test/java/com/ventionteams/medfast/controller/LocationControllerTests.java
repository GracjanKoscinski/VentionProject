package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.PatientProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.response.LocationResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.LocationService;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

/**
 * Tests the LocationController functionality with integration tests.
 */
@RequiredArgsConstructor
class LocationControllerTests extends IntegrationTest {

  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final PatientProvider patientProvider;
  private final EntityProvider<User> userProvider;
  @MockBean
  private LocationService locationService;
  private String patientToken;
  private Doctor doctor;
  private HospitalService service;
  private Location location;

  @BeforeAll
  void setUp() {
    service = hospitalServiceProvider.provide();
    Specialization specialization = specializationProvider.provide(List.of(service));
    location = locationProvider.provide();
    doctor = doctorProvider.provide(List.of(location, List.of(specialization)));
    Patient patient = patientProvider.provide();
    User userPatient = userProvider.provide(List.of(patient));
    Map<String, String> request = new HashMap<>();
    request.put("email", userPatient.getEmail());
    request.put("password", ((UserProvider) userProvider).getRawPassword(userPatient.getEmail()));

    patientToken = "Bearer " + given()
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
  void getAvailableLocations_WithDateTime_ReturnsListOfLocations() {
    LocationResponse locationResponse = LocationResponse.builder()
        .id(location.getId())
        .hospitalName(location.getHospitalName())
        .streetAddress(location.getStreetAddress())
        .house(location.getHouse())
        .build();

    when(locationService.getAvailableLocations(any(Long.class),
        any(Long.class), any(LocalDateTime.class)))
        .thenReturn(List.of(location));

    given()
        .param("serviceId", service.getId())
        .param("doctorId", doctor.getId())
        .param("dateTime", LocalDateTime.now().toString())
        .header("Authorization", patientToken)
        .when()
        .get("/api/locations")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(HttpStatus.OK.value()))
        .body("message", equalTo("Operation successful"))
        .body("data", hasSize(1))
        .body("data[0].id", equalTo(locationResponse.getId().intValue()))
        .body("data[0].hospitalName", equalTo(locationResponse.getHospitalName()))
        .body("data[0].streetAddress", equalTo(locationResponse.getStreetAddress()))
        .body("data[0].house", equalTo(locationResponse.getHouse()));
  }

  @Test
  void getAvailableLocations_WithoutDateTime_ReturnsListOfLocations() {
    LocationResponse locationResponse = LocationResponse.builder()
        .id(location.getId())
        .hospitalName(location.getHospitalName())
        .streetAddress(location.getStreetAddress())
        .house(location.getHouse())
        .build();

    when(locationService.getAvailableLocations(any(Long.class), any(Long.class), isNull()))
        .thenReturn(List.of(location));

    given()
        .param("serviceId", service.getId())
        .param("doctorId", doctor.getId())
        .header("Authorization", patientToken)
        .when()
        .get("/api/locations")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(HttpStatus.OK.value()))
        .body("message", equalTo("Operation successful"))
        .body("data", hasSize(1))
        .body("data[0].id", equalTo(locationResponse.getId().intValue()))
        .body("data[0].hospitalName", equalTo(locationResponse.getHospitalName()))
        .body("data[0].streetAddress", equalTo(locationResponse.getStreetAddress()))
        .body("data[0].house", equalTo(locationResponse.getHouse()));
  }

  @Test
  void getAvailableLocations_WithDateTime_ReturnsEmptyList() {
    when(locationService.getAvailableLocations(any(Long.class),
        any(Long.class), any(LocalDateTime.class)))
        .thenReturn(Collections.emptyList());

    given()
        .param("serviceId", service.getId())
        .param("doctorId", doctor.getId())
        .param("dateTime", LocalDateTime.now().toString())
        .header("Authorization", patientToken)
        .when()
        .get("/api/locations")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(HttpStatus.OK.value()))
        .body("message", equalTo("Operation successful"))
        .body("data", empty());
  }

  @Test
  void getAvailableLocations_WithoutDateTime_ReturnsEmptyList() {
    when(locationService.getAvailableLocations(any(Long.class),
        any(Long.class), isNull()))
        .thenReturn(Collections.emptyList());

    given()
        .param("serviceId", service.getId())
        .param("doctorId", doctor.getId())
        .header("Authorization", patientToken)
        .when()
        .get("/api/locations")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(HttpStatus.OK.value()))
        .body("message", equalTo("Operation successful"))
        .body("data", empty());
  }
}