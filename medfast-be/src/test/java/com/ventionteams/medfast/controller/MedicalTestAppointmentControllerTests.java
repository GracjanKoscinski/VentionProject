package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.RescheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import com.ventionteams.medfast.service.appointment.EmailAppointmentService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;


/**
 * Tests the medical appointment test controller functionality with integration tests.
 */
@RequiredArgsConstructor
public class MedicalTestAppointmentControllerTests extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<MedicalTestAppointment> medicalTestAppointmentProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<MedicalTest> medicalTestProvider;
  private final EntityProvider<Person> personProvider;
  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  // DEVNOTE: We mock the email service to avoid sending actual emails.
  @MockBean
  private final EmailAppointmentService emailService;
  private String jwtPatient;
  private ScheduleMedicalTestAppointmentRequest createMedicalTestAppointmentRequest;
  private String jwtAdmin;
  private Patient patient;
  private MedicalTestAppointment test;
  private Location newLocation;
  private Location location;
  private MedicalTest medicalTest;

  /**
   * Sets up entities before test.
   */
  @BeforeAll
  public void setUp() {
    patient = patientProvider.provide();
    User userPatient = userProvider.provide(List.of(patient));

    SignInRequest request = new SignInRequest(userPatient.getEmail(),
        ((UserProvider) userProvider).getRawPassword(userPatient.getEmail()));

    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");

    jwtPatient = response.body().jsonPath().getString("data.accessToken");

    User userAdmin = userProvider.provide(List.of(personProvider.provide()));
    request = new SignInRequest(userAdmin.getEmail(),
        ((UserProvider) userProvider).getRawPassword(userAdmin.getEmail()));

    response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");

    jwtAdmin = response.body().jsonPath().getString("data.accessToken");

    location = locationProvider.provide();
    newLocation = locationProvider.provide();
    medicalTest = medicalTestProvider.provide(List.of(location, newLocation));
    createMedicalTestAppointmentRequest = ScheduleMedicalTestAppointmentRequest.builder()
        .testId(medicalTest.getId())
        .locationId(location.getId())
        .dateTime(LocalDate.now().atTime(10, 30).plusDays(2).atZone(ZoneId.of("UTC")))
        .build();
    medicalTestAppointmentProvider.provide(List.of(
        patient, AppointmentRequestType.UPCOMING, medicalTest, location));
    test = medicalTestAppointmentProvider.provide(List.of(
        patient, AppointmentRequestType.PAST, medicalTest, location));

  }

  @Test
  public void scheduleAppointment_ValidRequest_CreatesAppointmentAndReturnsCreated() {
    Response response = given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtPatient)
        .body(createMedicalTestAppointmentRequest)
        .when()
        .post("/api/patient/test-appointments/schedule");

    if (response.getStatusCode() != 201) {
      throw new AssertionFailedError(
          "Request has not led to the creation of a resource, response status is "
              + response.getStatusCode());
    }

    Long appointmentId = response.body().jsonPath().getLong("data");
    MedicalTestAppointment foundAppointment = medicalTestAppointmentRepository
        .findById(appointmentId)
        .orElseThrow(() -> new AssertionFailedError("Medical test appointment was not created"));
    assertEquals(foundAppointment.getDateTime(),
        createMedicalTestAppointmentRequest.getDateTime().toLocalDateTime());
    assertEquals(foundAppointment.getLocation().getId(),
        createMedicalTestAppointmentRequest.getLocationId());
    assertEquals(foundAppointment.getTest().getId(),
        createMedicalTestAppointmentRequest.getTestId());
    assertEquals(foundAppointment.getStatus(), AppointmentStatus.SCHEDULED_CONFIRMED);
    medicalTestAppointmentRepository.deleteById(appointmentId);
  }

  @Test
  public void cancelAppointment_ValidRequest_CancelsAppointmentAndReturnsNoContent() {
    MedicalTestAppointment appointmentToCancel = medicalTestAppointmentProvider.provide(List.of(
        patient, AppointmentRequestType.UPCOMING, medicalTest, location));
    Long appointmentId = appointmentToCancel.getId();

    given()
        .contentType(ContentType.JSON)
        .body(createMedicalTestAppointmentRequest)
        .header("Authorization", "Bearer " + jwtPatient)
        .when()
        .put("/api/patient/test-appointments/" + appointmentId + "/cancel")
        .then().statusCode(204);

    MedicalTestAppointment cancelledAppointment = medicalTestAppointmentRepository.findById(
        appointmentId).get();
    assertEquals(cancelledAppointment.getStatus().name(),
        AppointmentStatus.CANCELLED_PATIENT.name(),
        "Appointment wasn't correctly cancelled");
    medicalTestAppointmentRepository.deleteById(appointmentId);
  }

  @Test
  public void getSortedTests_ValidRequest_ReturnsOk() {

    given()
        .contentType(ContentType.JSON)
        .param("type", "UPCOMING")
        .header("Authorization", "Bearer " + jwtPatient)
        .when()
        .get("/api/patient/test-appointments")
        .then()
        .statusCode(200)
        .body("data[0].testName", notNullValue())
        .body("data[0].status", notNullValue())
        .body("data[0].location", notNullValue())
        .body("data[0].hasPdfResult", equalTo(false))
        .body("data[0].dateTimeFrom", notNullValue())
        .body("data[0].dateTimeTo", notNullValue());

  }

  @Test
  public void generateTestResultForAppointment_ValidRequest_ReturnsOk() {
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtAdmin)
        .queryParam("testAppointmentId", test.getId())
        .when()
        .post("/api/patient/test-appointments/generate")
        .then()
        .statusCode(200)
        .body("message", equalTo("Test result has been generated successfully"));
  }

  @Test
  public void getTestResult_ValidRequest_ReturnsOkAndHeaderWithContent() {
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtAdmin)
        .queryParam("testAppointmentId", test.getId())
        .when()
        .post("/api/patient/test-appointments/generate");

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtPatient)
        .queryParam("testId", test.getId())
        .when()
        .get("/api/patient/test-appointments/result")
        .then()
        .statusCode(200)
        .header("Content-Type", "application/pdf")
        .body(notNullValue())
        .header("Content-Length", notNullValue());
  }

  @Test
  public void getOccupiedTimeslots_WithAllParameters_ReturnsOk() {
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtPatient)
        .queryParam("testId", medicalTest.getId())
        .queryParam("locationId", location.getId())
        .queryParam("month", 10)
        .queryParam("year", 2024)
        .when()
        .get("/api/patient/test-appointments/available-timeslots")
        .then()
        .statusCode(200)
        .body("data", notNullValue());
  }

  @Test
  public void getOccupiedTimeslots_WithMonthAndYear_ReturnsOk() {
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtPatient)
        .queryParam("testId", medicalTest.getId())
        .queryParam("month", 10)
        .queryParam("year", 2024)
        .when()
        .get("/api/patient/test-appointments/available-timeslots")
        .then()
        .statusCode(200)
        .body("data", notNullValue());
  }

  @Test
  public void getAvailableLocations_ValidTestId_ReturnsOk() {
    given()
        .contentType(ContentType.JSON)
        .param("testId", medicalTest.getId())
        .header("Authorization", "Bearer " + jwtPatient)
        .when()
        .get("/api/patient/test-appointments/available-locations")
        .then()
        .statusCode(200)
        .body("data", notNullValue())
        .body("data.size()", equalTo(2))
        .body("data[0].hospitalName", notNullValue());
  }

  @Test
  void rescheduleAppointment_ValidRequest_ReschedulesAppointmentAndReturnsNoContent() {
    MedicalTestAppointment appointmentToReschedule = medicalTestAppointmentProvider.provide(List.of(
        patient, AppointmentRequestType.UPCOMING, medicalTest, location));
    Long appointmentId = appointmentToReschedule.getId();
    RescheduleMedicalTestAppointmentRequest request = RescheduleMedicalTestAppointmentRequest
        .builder()
        .appointmentId(appointmentId)
        .locationId(newLocation.getId())
        .dateFrom(ZonedDateTime.of(
            LocalDateTime.of(LocalDate.now().plusDays(4), LocalTime.NOON),
            ZoneId.of("UTC")
        ))
        .build();

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwtPatient)
        .body(request)
        .when()
        .put("/api/patient/test-appointments/" + appointmentId + "/reschedule")
        .then().statusCode(204);

    MedicalTestAppointment rescheduledAppointment =
        medicalTestAppointmentRepository.findById(appointmentId).get();
    assertEquals(rescheduledAppointment.getStatus().name(),
        AppointmentStatus.SCHEDULED.name(), "Appointment wasn't correctly rescheduled");
    assertEquals(rescheduledAppointment.getLocation().getId(),
        newLocation.getId(), "Location id mismatch");
    assertEquals(rescheduledAppointment.getDateTime(),
        request.getDateFrom().toLocalDateTime(), "DateTime mismatch");
    medicalTestAppointmentRepository.deleteById(appointmentId);
  }
}
