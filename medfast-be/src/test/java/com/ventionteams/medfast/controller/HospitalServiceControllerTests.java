package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.PatientProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.response.HospitalServiceResponse;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Recommendation;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.enums.Gender;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the HospitalServiceController functionality with integration tests.
 */
@RequiredArgsConstructor
public class HospitalServiceControllerTests extends IntegrationTest {

  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final EntityProvider<Recommendation> recommendationProvider;
  private final PatientProvider patientProvider;
  private final EntityProvider<User> userProvider;
  private String patientToken;
  private HospitalService service;
  private List<HospitalServiceResponse> expectedServices = new ArrayList<>();

  /**
   * Prepares patient's access token.
   */
  @BeforeAll
  public void setup() {
    service = hospitalServiceProvider.provide();
    Map<String, String> request = new HashMap<>();
    Patient patient = patientProvider.provide();
    User userPatient = userProvider.provide(List.of(patient));
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

    int patientAge = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
    provideRecommendations(patientAge, patient.getSex());
  }

  @Test
  public void getAllServices_GoodRequest_ReturnsList() {
    List<HospitalServiceResponse> expectedData = List.of(HospitalServiceResponse.builder()
        .id(service.getId())
        .service(service.getService())
        .duration(service.getDuration())
        .build());

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", patientToken)
        .when()
        .get("/api/services")
        .then()
        .statusCode(200)
        .body("data", hasSize(21))
        .body("data[0].id", equalTo(expectedData.get(0).getId().intValue()))
        .body("data[0].service", equalTo(expectedData.get(0).getService()))
        .body("data[0].duration", equalTo(expectedData.get(0).getDuration().intValue()));
  }

  @Test
  public void getRecommendedServices_WithFirstElementSelection_ReturnsServicesList() {
    given()
        .contentType(ContentType.JSON)
        .param("elementSelection", ElementSelection.FIRST)
        .header("Authorization", patientToken)
        .when()
        .get("/api/services/recommendations")
        .then()
        .statusCode(200)
        .body("data", hasSize(2))
        .body("data[0].id", equalTo(expectedServices.get(0).getId().intValue()))
        .body("data[0].service", equalTo(expectedServices.get(0).getService()))
        .body("data[0].duration", equalTo(expectedServices.get(0).getDuration().intValue()))
        .body("data[1].id", equalTo(expectedServices.get(1).getId().intValue()))
        .body("data[1].service", equalTo(expectedServices.get(1).getService()))
        .body("data[1].duration", equalTo(expectedServices.get(1).getDuration().intValue()))
        .body("message", equalTo("Operation successful"));
  }

  @Test
  public void getRecommendedServices_WithRemainingElementSelection_ReturnsServicesList() {
    given()
        .contentType(ContentType.JSON)
        .param("elementSelection", ElementSelection.REMAINING)
        .header("Authorization", patientToken)
        .when()
        .get("/api/services/recommendations")
        .then()
        .statusCode(200)
        .body("data", hasSize(expectedServices.size() - 2))
        .body("data[0].id", equalTo(expectedServices.get(2).getId().intValue()))
        .body("data[0].service", equalTo(expectedServices.get(2).getService()))
        .body("data[0].duration", equalTo(expectedServices.get(2).getDuration().intValue()))
        .body("data[1].id", equalTo(expectedServices.get(3).getId().intValue()))
        .body("data[1].service", equalTo(expectedServices.get(3).getService()))
        .body("data[1].duration", equalTo(expectedServices.get(3).getDuration().intValue()))
        .body("message", equalTo("Operation successful"));
  }

  private void provideRecommendations(int patientAge, Gender patientGender) {
    for (int i = 0; i < 20; i++) {
      HospitalService tempService = hospitalServiceProvider.provide();
      Recommendation tempRecommendation = createRecommendation(i, tempService, patientAge);

      if (isRecommendationValid(tempRecommendation, patientAge, patientGender)) {
        expectedServices.add(buildServiceResponse(tempService));
      }
    }

    expectedServices = expectedServices.stream()
        .sorted(Comparator.comparing(HospitalServiceResponse::getService,
            String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private Recommendation createRecommendation(int index, HospitalService service, int age) {
    Gender recommendationGender;

    if (index > 14) {
      return recommendationProvider.provide(List.of(service));
    } else if (index % 3 == 0) {
      recommendationGender = Gender.MALE;
    } else if (index % 2 == 0) {
      recommendationGender = Gender.FEMALE;
    } else {
      recommendationGender = Gender.NEUTRAL;
    }

    return recommendationProvider.provide(List.of(service, age, recommendationGender));
  }

  private boolean isRecommendationValid(Recommendation recommendation, int age, Gender gender) {
    return recommendation.getAgeFrom() <= age
        && recommendation.getAgeTo() >= age
        && (Objects.equals(recommendation.getLegalGender(), Gender.NEUTRAL)
        || Objects.equals(recommendation.getLegalGender(), gender));
  }

  private HospitalServiceResponse buildServiceResponse(HospitalService service) {
    return HospitalServiceResponse.builder()
        .service(service.getService())
        .duration(service.getDuration())
        .id(service.getId())
        .build();
  }
}
