package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.mapper.doctor.DoctorToResponseMapper;
import com.ventionteams.medfast.repository.UserRepository;
import io.restassured.http.ContentType;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Tests the admin controller functionality with integration tests.
 */
@RequiredArgsConstructor
public class AdminControllerTests extends IntegrationTest {

  private final DoctorToResponseMapper doctorToResponseMapper;
  private final EntityProvider<Specialization> specializationProvider;
  private final EntityProvider<Location> locationProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<Doctor> doctorProvider;
  private final EntityProvider<HospitalService> hospitalServiceProvider;
  private final EntityProvider<Person> personProvider;
  private final UserRepository userRepository;
  private final Faker faker;
  @MockBean
  private JavaMailSender mailSender;
  private String adminToken;
  private Doctor doctor;
  private User doctorUser;
  private Location location;
  private Specialization specialization;
  private List<DoctorResponse> doctorResponse;

  /**
   * Prepares an admin access token.
   */
  @BeforeAll
  public void setup() {
    HospitalService service = hospitalServiceProvider.provide();
    specialization = specializationProvider.provide(List.of(service));
    location = locationProvider.provide();
    doctor = doctorProvider.provide(List.of(location, List.of(specialization)));
    doctorUser = userProvider.provide(List.of(doctor));
    doctorResponse = doctorToResponseMapper.apply(List.of(doctor));
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
  public void registerDoctor_ValidRequest_ReturnsCreated() {
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    Mockito.doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));
    DoctorRegistrationRequest request = DoctorRegistrationRequest.builder()
        .email(faker.internet().emailAddress())
        .name(faker.name().firstName())
        .surname(faker.name().lastName())
        .birthDate(faker.timeAndDate().birthday())
        .phone(faker.phoneNumber().subscriberNumber(11))
        .specializationIds(List.of(specialization.getId()))
        .locationId(location.getId())
        .licenseNumber("1234567")
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .header("Authorization", adminToken)
        .when()
        .post("api/admin-console/registerDoctor")
        .then()
        .statusCode(201)
        .body("data", nullValue())
        .body("message", equalTo("Operation successful"));

    if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
      throw new AssertionFailedError("Doctor was not created");
    }
  }

  @Test
  public void getDoctors_ValidRequest_ReturnsList() {

    given()
        .contentType(ContentType.JSON)
        .param("amount", "1")
        .param("sortBy", "EMAIL")
        .header("Authorization", adminToken)
        .when()
        .get("api/admin-console/get-doctors")
        .then()
        .statusCode(200)
        .body("message", equalTo("Operation successful"))
        .body("data.doctors[0].name", equalTo(doctor.getName()
            .concat(" ").concat(doctor.getSurname())))
        .body("data.doctors[0].email", equalTo(doctorUser.getEmail()))
        .body("data.doctors[0].specializations", equalTo(
            doctorResponse.get(0).getSpecializations()))
        .body("data.doctors[0].status", equalTo(doctorUser.getUserStatus().name()));
  }

  @Test
  public void searchDoctors_ValidRequest_ReturnsList() {
    String doctorUserEmail = doctorUser.getEmail();

    given()
        .contentType(ContentType.JSON)
        .param("keyword", doctorUserEmail.substring(
            1, Math.min(doctorUserEmail.length(), 4)))
        .header("Authorization", adminToken)
        .when()
        .get("api/admin-console/search-doctors")
        .then()
        .statusCode(200)
        .body("message", equalTo("Operation successful"));
  }
}