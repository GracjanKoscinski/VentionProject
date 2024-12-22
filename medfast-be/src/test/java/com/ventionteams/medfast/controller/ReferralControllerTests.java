package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.response.ReferralResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Referral;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.mapper.ReferralsToResponseMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the referral controller functionality with integration tests.
 */
@RequiredArgsConstructor
public class ReferralControllerTests extends IntegrationTest {

  private final EntityProvider<Referral> referralProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<ConsultationAppointment> appointmentProvider;
  private final ReferralsToResponseMapper referralsToResponseMapper;
  private String patientToken;
  private List<ReferralResponse> upcomingReferrals = new ArrayList<>();
  private List<ReferralResponse> pastReferrals = new ArrayList<>();

  @BeforeAll
  void setUp() {
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

    HospitalService service = hospitalServiceProvider.provide();
    Specialization specialization = specializationProvider.provide(List.of(service));
    Location location = locationProvider.provide();

    provideReferrals(patient, service, specialization, location);

    pastReferrals = pastReferrals.stream()
        .sorted(Comparator.comparing(ReferralResponse::getDateOfIssue).reversed()).toList();
    upcomingReferrals = upcomingReferrals.stream()
        .sorted(Comparator.comparing(ReferralResponse::getDateOfIssue).reversed()).toList();
  }

  @Test
  void getReferrals_UpcomingFirst_ReturnsReferralsAndReturnsOk() {
    Response response = given()
        .param("referralType", "UPCOMING")
        .param("elementSelection", "FIRST")
        .header("Authorization", patientToken)
        .when()
        .get("/api/patient/referrals");

    response.then()
        .statusCode(200)
        .body("data", hasSize(3))
        .body("data[0].id", equalTo(upcomingReferrals.get(0).getId().intValue()))
        .body("data[0].issuedBy", equalTo(upcomingReferrals.get(0).getIssuedBy()))
        .body("data[0].specialization", equalTo(upcomingReferrals.get(0).getSpecialization()))
        .body("data[0].dateOfIssue", equalTo(upcomingReferrals.get(0).getDateOfIssue().toString()))
        .body("data[0].expirationDate",
            equalTo(upcomingReferrals.get(0).getExpirationDate().toString()))
        .body("data[0].appointmentStatus", equalTo(upcomingReferrals.get(0).getAppointmentStatus()))
        .body("data[0].appointmentId", equalTo(upcomingReferrals.get(0).getAppointmentId() == null
            ? null : upcomingReferrals.get(0).getAppointmentId().intValue()))
        .body("data[1].id", equalTo(upcomingReferrals.get(1).getId().intValue()))
        .body("data[1].issuedBy", equalTo(upcomingReferrals.get(1).getIssuedBy()))
        .body("data[1].specialization", equalTo(upcomingReferrals.get(1).getSpecialization()))
        .body("data[1].dateOfIssue", equalTo(upcomingReferrals.get(1).getDateOfIssue().toString()))
        .body("data[1].expirationDate",
            equalTo(upcomingReferrals.get(1).getExpirationDate().toString()))
        .body("data[1].appointmentStatus", equalTo(upcomingReferrals.get(1).getAppointmentStatus()))
        .body("data[1].appointmentId", equalTo(upcomingReferrals.get(1).getAppointmentId() == null
            ? null : upcomingReferrals.get(1).getAppointmentId().intValue()));
  }

  @Test
  void getReferrals_UpcomingRemaining_ReturnsReferralsAndReturnsOk() {
    Response response = given()
        .param("referralType", "UPCOMING")
        .param("elementSelection", "REMAINING")
        .header("Authorization", patientToken)
        .when()
        .get("/api/patient/referrals");

    response.then()
        .statusCode(200)
        .body("data[0].id", equalTo(upcomingReferrals.get(3).getId().intValue()))
        .body("data[0].issuedBy", equalTo(upcomingReferrals.get(3).getIssuedBy()))
        .body("data[0].specialization", equalTo(upcomingReferrals.get(3).getSpecialization()))
        .body("data[0].dateOfIssue", equalTo(upcomingReferrals.get(3).getDateOfIssue().toString()))
        .body("data[0].expirationDate",
            equalTo(upcomingReferrals.get(3).getExpirationDate().toString()))
        .body("data[0].appointmentStatus", equalTo(upcomingReferrals.get(3).getAppointmentStatus()))
        .body("data[0].appointmentId", equalTo(upcomingReferrals.get(3).getAppointmentId() == null
            ? null : upcomingReferrals.get(3).getAppointmentId().intValue()))
        .body("data[1].id", equalTo(upcomingReferrals.get(4).getId().intValue()))
        .body("data[1].issuedBy", equalTo(upcomingReferrals.get(4).getIssuedBy()))
        .body("data[1].specialization", equalTo(upcomingReferrals.get(4).getSpecialization()))
        .body("data[1].dateOfIssue", equalTo(upcomingReferrals.get(4).getDateOfIssue().toString()))
        .body("data[1].expirationDate",
            equalTo(upcomingReferrals.get(4).getExpirationDate().toString()))
        .body("data[1].appointmentStatus", equalTo(upcomingReferrals.get(4).getAppointmentStatus()))
        .body("data[1].appointmentId", equalTo(upcomingReferrals.get(4).getAppointmentId() == null
            ? null : upcomingReferrals.get(4).getAppointmentId().intValue()));
  }

  @Test
  void getReferrals_PastFirst_ReturnsReferralsAndReturnsOk() {
    Response response = given()
        .param("referralType", "PAST")
        .param("elementSelection", "FIRST")
        .header("Authorization", patientToken)
        .when()
        .get("/api/patient/referrals");

    response.then()
        .statusCode(200)
        .body("data", hasSize(3))
        .body("data[0].id", equalTo(pastReferrals.get(0).getId().intValue()))
        .body("data[0].issuedBy", equalTo(pastReferrals.get(0).getIssuedBy()))
        .body("data[0].specialization", equalTo(pastReferrals.get(0).getSpecialization()))
        .body("data[0].dateOfIssue", equalTo(pastReferrals.get(0).getDateOfIssue().toString()))
        .body("data[0].expirationDate",
            equalTo(pastReferrals.get(0).getExpirationDate().toString()))
        .body("data[0].appointmentStatus", equalTo(pastReferrals.get(0).getAppointmentStatus()))
        .body("data[0].appointmentId", equalTo(pastReferrals.get(0).getAppointmentId() == null
            ? null : pastReferrals.get(0).getAppointmentId().intValue()))
        .body("data[1].id", equalTo(pastReferrals.get(1).getId().intValue()))
        .body("data[1].issuedBy", equalTo(pastReferrals.get(1).getIssuedBy()))
        .body("data[1].specialization", equalTo(pastReferrals.get(1).getSpecialization()))
        .body("data[1].dateOfIssue", equalTo(pastReferrals.get(1).getDateOfIssue().toString()))
        .body("data[1].expirationDate",
            equalTo(pastReferrals.get(1).getExpirationDate().toString()))
        .body("data[1].appointmentStatus", equalTo(pastReferrals.get(1).getAppointmentStatus()))
        .body("data[1].appointmentId", equalTo(pastReferrals.get(1).getAppointmentId() == null
            ? null : pastReferrals.get(1).getAppointmentId().intValue()));
  }

  @Test
  void getReferrals_PastRemaining_ReturnsReferralsAndReturnsOk() {
    given()
        .param("referralType", "PAST")
        .param("elementSelection", "REMAINING")
        .header("Authorization", patientToken)
        .when()
        .get("/api/patient/referrals")
        .then()
        .statusCode(200)
        .body("data[0].id", equalTo(pastReferrals.get(3).getId().intValue()))
        .body("data[0].issuedBy", equalTo(pastReferrals.get(3).getIssuedBy()))
        .body("data[0].specialization", equalTo(pastReferrals.get(3).getSpecialization()))
        .body("data[0].dateOfIssue", equalTo(pastReferrals.get(3).getDateOfIssue().toString()))
        .body("data[0].expirationDate",
            equalTo(pastReferrals.get(3).getExpirationDate().toString()))
        .body("data[0].appointmentStatus", equalTo(pastReferrals.get(3).getAppointmentStatus()))
        .body("data[0].appointmentId", equalTo(pastReferrals.get(3).getAppointmentId() == null
            ? null : pastReferrals.get(3).getAppointmentId().intValue()))
        .body("data[1].id", equalTo(pastReferrals.get(4).getId().intValue()))
        .body("data[1].issuedBy", equalTo(pastReferrals.get(4).getIssuedBy()))
        .body("data[1].specialization", equalTo(pastReferrals.get(4).getSpecialization()))
        .body("data[1].dateOfIssue", equalTo(pastReferrals.get(4).getDateOfIssue().toString()))
        .body("data[1].expirationDate",
            equalTo(pastReferrals.get(4).getExpirationDate().toString()))
        .body("data[1].appointmentStatus", equalTo(pastReferrals.get(4).getAppointmentStatus()))
        .body("data[1].appointmentId", equalTo(pastReferrals.get(4).getAppointmentId() == null
            ? null : pastReferrals.get(4).getAppointmentId().intValue()));
  }

  private void provideReferrals(Patient patient, HospitalService service,
      Specialization specialization, Location location) {
    for (int i = 0; i < 14; i++) {
      Doctor tempDoctor = doctorProvider.provide(List.of(
          locationProvider.provide(),
          List.of(specializationProvider.provide(List.of(
              hospitalServiceProvider.provide())))));
      Referral tempReferral = provideReferral(
          i, tempDoctor, patient, service, specialization, location);

      if (
          tempReferral.getExpirationDate().isBefore(LocalDate.now())
              || tempReferral.getConsultationAppointment() != null
      ) {
        pastReferrals.add(referralsToResponseMapper.apply(List.of(tempReferral)).get(0));
      } else {
        upcomingReferrals.add(referralsToResponseMapper.apply(List.of(tempReferral)).get(0));
      }
    }
  }

  private Referral provideReferral(int i, Doctor tempDoctor, Patient patient,
      HospitalService service, Specialization specialization, Location location) {
    return i % 2 == 0 ? referralProvider.provide(List.of(
        tempDoctor,
        specialization,
        patient,
        i < 2 ? AppointmentRequestType.PAST : AppointmentRequestType.UPCOMING
    )) : referralProvider.provide(List.of(
        tempDoctor,
        specialization,
        patient,
        i < 2 ? AppointmentRequestType.PAST : AppointmentRequestType.UPCOMING,
        appointmentProvider.provide(List.of(
            doctorProvider.provide(List.of(location, List.of(specialization))),
            patient,
            service
        ))
    ));
  }
}
