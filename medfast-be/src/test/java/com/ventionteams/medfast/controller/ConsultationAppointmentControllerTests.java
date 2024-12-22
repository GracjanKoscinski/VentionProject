package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.RescheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.ConsultationAppointmentType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.service.appointment.EmailAppointmentService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests the appointment controller functionality with integration tests.
 */
@RequiredArgsConstructor()
class ConsultationAppointmentControllerTests extends IntegrationTest {

  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<ConsultationAppointment> consultationAppointmentProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final AppointmentRepository consultationAppointmentRepository;

  // DEVNOTE: We mock the email service to avoid sending actual emails.
  @MockBean
  private final EmailAppointmentService emailService;
  private HospitalService service;
  private Location location;
  private Patient patient;
  private String jwt;
  private Doctor doctor;
  private ConsultationAppointment appointment;
  private Specialization specialization;
  private List<ConsultationAppointment> appointmentsSortedByDateFrom = new ArrayList<>();
  private List<Long> doctorIdsSortedByDateFrom = new ArrayList<>();
  private List<String> doctorFullNamesSortedByDateFrom = new ArrayList<>();
  private List<List<String>> doctorSpecializationsSortedByDateFrom = new ArrayList<>();

  /**
   * Setups and integrates the necessary entities into db for use in further tests.
   */
  @BeforeAll
  void setUp() {
    service = hospitalServiceProvider.provide();
    specialization = specializationProvider.provide(List.of(service));

    location = locationProvider.provide();
    doctor = doctorProvider.provide(
        List.of(location, List.of(specialization)));
    patient = patientProvider.provide();
    appointment = consultationAppointmentProvider.provide(List.of(
        doctor, patient, AppointmentStatus.COMPLETED, service));
    appointmentsSortedByDateFrom.add(appointment);

    for (int i = 0; i < 3; i++) {
      Doctor tempDoctor = doctorProvider.provide(
          List.of(location, List.of(specializationProvider.provide(List.of(service)))));
      appointmentsSortedByDateFrom.add(
          consultationAppointmentProvider.provide(List.of(
              tempDoctor,
              patient,
              AppointmentStatus.COMPLETED,
              service)));
    }

    appointmentsSortedByDateFrom = appointmentsSortedByDateFrom.stream()
        .sorted(Comparator.comparing(ConsultationAppointment::getDateFrom).reversed())
        .toList();
    for (ConsultationAppointment a : appointmentsSortedByDateFrom) {
      Doctor tempDoctor = a.getDoctor();
      doctorIdsSortedByDateFrom.add(tempDoctor.getId());
      doctorFullNamesSortedByDateFrom.add(tempDoctor.getName() + " " + tempDoctor.getSurname());
      doctorSpecializationsSortedByDateFrom.add(tempDoctor.getSpecializations().stream().map(
          Specialization::getSpecialization).toList());
    }

    User patientUser = userProvider.provide(List.of(patient));
    SignInRequest request = new SignInRequest(patientUser.getEmail(),
        ((UserProvider) userProvider).getRawPassword(patientUser.getEmail()));

    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");

    jwt = response.body().jsonPath().getString("data.accessToken");
  }

  @Test
  void getAppointments_ValidRequest_ReturnsAppointmentsAndReturnsOk() {
    Response response = given()
        .param("type", "UPCOMING")
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/patient/appointments");

    List<String> dateFromList = response.jsonPath().getList("data.dateFrom");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    List<String> sortedDateFromList = dateFromList.stream()
        .map(date -> LocalDateTime.parse(date, formatter))
        .sorted()
        .map(date -> date.format(formatter))
        .toList();

    response.then()
        .statusCode(200)
        .body("data", hasSize(4))
        .body("data.dateFrom", equalTo(sortedDateFromList))
        .body(String.format("data.find { it.id == %d }.doctorSummary.id", appointment.getId()),
            equalTo(doctor.getId().intValue()))
        .body(String.format("data.find { it.id == %d }.doctorSummary.specializations[0]",
                appointment.getId()),
            equalTo(doctor.getSpecializations().get(0).getSpecialization()))
        .body(
            String.format("data.find { it.id == %d }.doctorSummary.fullName", appointment.getId()),
            equalTo(doctor.getName() + " " + doctor.getSurname()))
        .body(String.format("data.find { it.id == %d }.dateFrom", appointment.getId()),
            notNullValue())
        .body(String.format("data.find { it.id == %d }.location", appointment.getId()),
            anyOf(containsString("Hospital"), emptyString()))
        .body(String.format("data.find { it.id == %d }.status", appointment.getId()),
            equalTo(AppointmentStatus.COMPLETED.toString()))
        .body(String.format("data.find { it.id == %d }.doctorSummary.userPhotoResponse",
            appointment.getId()), notNullValue())
        .body(String.format("data.find { it.id == %d }.type", appointment.getId()), anyOf(
            equalTo(ConsultationAppointmentType.ONSITE.toString()),
            equalTo(ConsultationAppointmentType.ONLINE.toString())));
  }

  @Test
  void scheduleOnlineAppointment_ValidRequest_CreatesAppointmentAndReturnsCreated()
      throws MessagingException {

    ScheduleConsultationAppointmentRequest request = ScheduleConsultationAppointmentRequest
        .builder()
        .serviceId(service.getId())
        .doctorId(doctor.getId())
        .dateFrom(ZonedDateTime.of(
            LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.NOON),
            ZoneId.of("UTC")
        ))
        .build();

    Response response = given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .post("/api/patient/appointments");

    if (response.getStatusCode() != 201) {
      throw new AssertionFailedError(
          "Request has not led to the creation of a resource, response status is "
              + response.getStatusCode());
    }

    Long appointmentId = response.body().jsonPath().getLong("data");
    ConsultationAppointment foundAppointment = consultationAppointmentRepository
        .findById(appointmentId)
        .orElseThrow(() -> new AssertionFailedError("Consultation appointment was not created"));
    assertEquals(foundAppointment.getDateFrom(), request.getDateFrom().toLocalDateTime());
    assertEquals(foundAppointment.getDoctor().getId(), doctor.getId());
    assertEquals(foundAppointment.getService().getId(), service.getId());
    assertNull(foundAppointment.getLocation());
    assertEquals(foundAppointment.getType(), ConsultationAppointmentType.ONLINE);
    assertEquals(foundAppointment.getStatus(), AppointmentStatus.SCHEDULED_CONFIRMED);
    consultationAppointmentRepository.deleteById(appointmentId);
  }

  @Test
  void scheduleOnSiteAppointment_ValidRequest_CreatesAppointmentAndReturnsCreated() {

    ScheduleConsultationAppointmentRequest request = ScheduleConsultationAppointmentRequest
        .builder()
        .serviceId(service.getId())
        .doctorId(doctor.getId())
        .locationId(location.getId())
        .dateFrom(ZonedDateTime.of(
            LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.NOON),
            ZoneId.of("UTC")
        ))
        .build();

    Response response = given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .post("/api/patient/appointments");

    if (response.getStatusCode() != 201) {
      throw new AssertionFailedError(
          "Request has not led to the creation of a resource, response status is "
              + response.getStatusCode());
    }

    Long appointmentId = response.body().jsonPath().getLong("data");
    ConsultationAppointment foundAppointment = consultationAppointmentRepository
        .findById(appointmentId)
        .orElseThrow(() -> new AssertionFailedError("Consultation appointment was not created"));
    assertEquals(foundAppointment.getDateFrom(), request.getDateFrom().toLocalDateTime());
    assertEquals(foundAppointment.getDoctor().getId(), doctor.getId());
    assertEquals(foundAppointment.getService().getId(), service.getId());
    assertEquals(foundAppointment.getLocation().getId(), location.getId());
    assertEquals(foundAppointment.getType(), ConsultationAppointmentType.ONSITE);
    assertEquals(foundAppointment.getStatus(), AppointmentStatus.SCHEDULED_CONFIRMED);
    consultationAppointmentRepository.deleteById(appointmentId);
  }

  @Test
  void getCareTeam_ValidRequestWithElementSelection_ReturnsList() {
    Response response = given()
        .param("elementSelection", ElementSelection.FIRST)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/patient/care-team");

    List<Integer> expectedDoctorIds = doctorIdsSortedByDateFrom.stream()
        .map(Long::intValue).limit(3).toList();
    List<String> expectedFullNames = doctorFullNamesSortedByDateFrom.stream()
        .limit(3).toList();
    List<List<String>> expectedSpecializations = doctorSpecializationsSortedByDateFrom.stream()
        .limit(3).toList();

    List<String> userPhotoBase64List = response.jsonPath()
        .getList("data.userPhotoResponse.userPhoto");
    userPhotoBase64List.stream()
        .map(base64 -> Base64.getDecoder().decode(base64))
        .limit(3)
        .toList();

    response.then()
        .statusCode(200)
        .body("data", hasSize(3))
        .body("data.id", equalTo(expectedDoctorIds))
        .body("data.fullName", equalTo(expectedFullNames))
        .body("data.specializations", equalTo(expectedSpecializations));
  }

  @Test
  void getCareTeam_ValidRequestWithoutElementSelection_ReturnsList() {
    Response response = given()
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/patient/care-team");

    List<Integer> expectedDoctorIds = doctorIdsSortedByDateFrom.stream()
        .map(Long::intValue).skip(3).toList();
    List<String> expectedFullNames = doctorFullNamesSortedByDateFrom.stream()
        .skip(3).toList();
    List<List<String>> expectedSpecializations = doctorSpecializationsSortedByDateFrom.stream()
        .skip(3).toList();

    List<String> userPhotoBase64List = response.jsonPath()
        .getList("data.userPhotoResponse.userPhoto");
    userPhotoBase64List.stream()
        .map(base64 -> Base64.getDecoder().decode(base64))
        .skip(3)
        .toList();

    response.then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data.id", equalTo(expectedDoctorIds))
        .body("data.fullName", equalTo(expectedFullNames))
        .body("data.specializations", equalTo(expectedSpecializations));
  }

  @Test
  void getAppointment_ValidRequest_ReturnsAppointment() {
    given()
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/patient/appointments/" + appointmentsSortedByDateFrom.get(0).getId())
        .then()
        .statusCode(200)
        .body("data.id", equalTo(appointmentsSortedByDateFrom.get(0).getId().intValue()))
        .body("data.dateFrom",
            equalTo(appointmentsSortedByDateFrom.get(0).getDateFrom().toString()))
        .body("data.dateTo", equalTo(appointmentsSortedByDateFrom.get(0).getDateTo().toString()))
        .body("data.location", equalTo(appointmentsSortedByDateFrom.get(0).getLocation() == null
            ? "" : appointmentsSortedByDateFrom.get(0).getLocation().toString()))
        .body("data.status", equalTo(appointmentsSortedByDateFrom.get(0).getStatus().toString()))
        .body("data.type", equalTo(appointmentsSortedByDateFrom.get(0).getType().toString()))
        .body("data.doctorSummary.id",
            equalTo(appointmentsSortedByDateFrom.get(0).getDoctor().getId().intValue()));
  }

  @Test
  void rescheduleAppointment_ValidRequest_ReschedulesAppointmentAndReturnsNoContent() {
    ConsultationAppointment appointmentToReschedule = consultationAppointmentProvider.provide(
        List.of(doctor, patient, AppointmentStatus.CANCELLED_PATIENT, service));
    Long appointmentId = appointmentToReschedule.getId();
    Location newLocation = locationProvider.provide();
    Doctor newDoctor = doctorProvider.provide(List.of(newLocation,
            List.of(specializationProvider.provide(List.of(service)))));
    RescheduleConsultationAppointmentRequest request = RescheduleConsultationAppointmentRequest
        .builder()
        .appointmentId(appointmentId)
        .doctorId(newDoctor.getId())
        .locationId(newLocation.getId())
        .dateFrom(ZonedDateTime.of(
            LocalDateTime.of(LocalDate.now().plusDays(4), LocalTime.NOON),
            ZoneId.of("UTC")
        ))
        .build();

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .put("/api/patient/appointments/" + appointmentId + "/reschedule")
        .then().statusCode(204);

    ConsultationAppointment rescheduledAppointment =
        consultationAppointmentRepository.findById(appointmentId).get();
    assertEquals(rescheduledAppointment.getStatus().name(),
        AppointmentStatus.SCHEDULED.name(), "Appointment wasn't correctly rescheduled");
    assertEquals(rescheduledAppointment.getLocation().getId(),
        newLocation.getId(), "Location id mismatch");
    assertEquals(rescheduledAppointment.getDoctor().getId(),
        newDoctor.getId(), "Doctor id mismatch");
    assertEquals(rescheduledAppointment.getDateFrom(),
        request.getDateFrom().toLocalDateTime(), "DateTime mismatch");
    consultationAppointmentRepository.deleteById(appointmentId);
  }

  @Test
  void cancelAppointment_ValidRequest_CancelsAppointmentAndReturnsNoContent() {
    ConsultationAppointment appointmentToCancel = consultationAppointmentProvider.provide(List.of(
        doctor,
        patient,
        AppointmentStatus.SCHEDULED,
        service));

    Long appointmentId = appointmentToCancel.getId();
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .put("/api/patient/appointments/" + appointmentId + "/cancel")
        .then().statusCode(204);

    ConsultationAppointment cancelledAppointment =
        consultationAppointmentRepository.findById(appointmentId).get();
    assertEquals(cancelledAppointment.getStatus().name(),
        AppointmentStatus.CANCELLED_PATIENT.name(),
        "Appointment wasn't correctly cancelled");
    consultationAppointmentRepository.deleteById(appointmentId);
  }
}
