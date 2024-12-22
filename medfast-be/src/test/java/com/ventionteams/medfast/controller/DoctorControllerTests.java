package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.TimeSlotService;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

@RequiredArgsConstructor
class DoctorControllerTests extends IntegrationTest {

  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final EntityProvider<Specialization> specializationProvider;

  @MockBean
  private TimeSlotService timeSlotService;

  private String jwt;
  private Doctor doctor;
  private HospitalService service;

  @BeforeAll
  void setUp() {
    Patient patient = patientProvider.provide();
    service = hospitalServiceProvider.provide();
    Location location = locationProvider.provide();
    User user = userProvider.provide(List.of(patient));
    Specialization specialization = specializationProvider.provide(List.of(service));
    doctor = doctorProvider.provide(List.of(location, List.of(specialization)));

    SignInRequest request = new SignInRequest(user.getEmail(),
        ((UserProvider) userProvider).getRawPassword(user.getEmail()));

    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");

    jwt = response.body().jsonPath().getString("data.accessToken");
  }

  @Test
  void getOccupiedTimeSlots_ValidRequest_ReturnsOccupiedSlots() {
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();
    TimeSlotResponse mockTimeSlot = new TimeSlotResponse();
    mockTimeSlot.setStartTime(startTime.toString());
    mockTimeSlot.setEndTime(endTime.toString());

    when(timeSlotService.getOccupiedTimeSlotsForPatientAndDoctor(
        Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(List.of(mockTimeSlot));

    int currentMonth = LocalDate.now().getMonthValue();
    int currentYear = LocalDate.now().getYear();
    given()
        .param("serviceId", service.getId())
        .param("month", currentMonth)
        .param("year", currentYear)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/doctors/" + doctor.getId() + "/appointments/occupied")
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].startTime", notNullValue())
        .body("data[0].endTime", notNullValue());
  }

  /**
   * Test case to verify doctors list returned for valid service.
   */
  @Test
  public void getServiceDoctors_GoodRequest_ReturnsDoctorsList() {
    String fullNamePart = doctor.getName().substring(2) + " "
        + doctor.getSurname().substring(0, 2);
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .param("fullName", fullNamePart)
        .when()
        .get("/api/doctors/services/" + service.getId())
        .then()
        .statusCode(200)
        .body("data", hasSize(1))
        .body("data[0].fullName",
            equalTo(doctor.getName() + " " + doctor.getSurname()))
        .body("data[0].specializations", notNullValue())
        .body("data[0].availableSlots", notNullValue())
        .body("data[0].specializations[0]", notNullValue())
        .body("message", equalTo("Operation successful"));
  }

  @Test
  void getAvailableTimeSlots_ValidRequest_ReturnsAvailableSlots() {
    Integer month = LocalDate.now().getMonthValue();
    Integer year = LocalDate.now().getYear();
    Long locationId = 1L;

    TimeSlot mockTimeSlot1 = TimeSlot.builder()
        .startTime(LocalDateTime.of(year, month, 10, 10, 0)) 
        .endTime(LocalDateTime.of(year, month, 10, 10, 30)) 
        .build();

    TimeSlot mockTimeSlot2 = TimeSlot.builder()
        .startTime(LocalDateTime.of(year, month, 11, 11, 0)) 
        .endTime(LocalDateTime.of(year, month, 11, 11, 30))
        .build();

    List<TimeSlot> mockTimeSlots = List.of(mockTimeSlot1, mockTimeSlot2);

    when(timeSlotService.getAvailableTimeSlotsForPatientAndDoctor(
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyLong(), 
            Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(mockTimeSlots);

    given()
        .header("Authorization", "Bearer " + jwt)
        .param("doctorId", doctor.getId())
        .param("serviceId", service.getId())
        .param("locationId", locationId)
        .param("month", month)
        .param("year", year)
        .when()
        .get("/api/doctors/available-slots")
        .then()
        .statusCode(200)
        .body("data", hasSize(2))
        .body("data[0].startTime", notNullValue())
        .body("data[0].endTime", notNullValue())
        .body("data[1].startTime", notNullValue())
        .body("data[1].endTime", notNullValue())
        .body("message", equalTo("Operation successful"));
  }
}
