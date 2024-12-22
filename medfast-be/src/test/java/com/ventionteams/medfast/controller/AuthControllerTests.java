package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.repository.UserRepository;
import com.ventionteams.medfast.repository.VerificationTokenRepository;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.auth.EmailVerificationService;
import io.restassured.http.ContentType;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;
import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * Tests the authorization controller functionality with integration tests.
 */
@RequiredArgsConstructor
class AuthControllerTests extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;
  private final EntityProvider<RefreshToken> refreshTokenProvider;
  private final Faker faker;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;

  @SpyBean
  private EmailService emailService;
  @SpyBean
  private EmailVerificationService emailVerificationService;

  private User user;
  PatientRegistrationRequest patientRegistrationRequest;

  @BeforeAll
  void setUp() {
    String streetAddress = faker.address().streetAddress().chars().limit(50)
        .mapToObj(c -> String.valueOf((char) c))
        .collect(Collectors.joining());
    String password = faker.internet().password(10, 50, true,
        true, true);

    user = userProvider.provide(List.of(patientProvider.provide()));
    patientRegistrationRequest = PatientRegistrationRequest.builder()
        .name(faker.name().malefirstName())
        .surname(faker.name().lastName())
        .birthDate(faker.timeAndDate().birthday())
        .phone(faker.phoneNumber().subscriberNumber(11))
        .password(password)
        .streetAddress(streetAddress)
        .house(faker.address().streetAddressNumber())
        .apartment(faker.address().buildingNumber())
        .city(faker.address().city())
        .state(faker.address().state())
        .zip(faker.address().zipCode())
        .sex(Gender.MALE)
        .citizenship(faker.country().name())
        .checkboxTermsAndConditions(true)
        .build();
  }

  @Test
  void signIn_ValidRequest_CreatesTokenAndReturnsOk() {

    SignInRequest request = new SignInRequest(user.getEmail(),
        ((UserProvider) userProvider).getRawPassword(user.getEmail()));

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin")
        .then()
        .statusCode(200)
        .body("data.accessToken", notNullValue())
        .body("data.refreshToken", notNullValue())
        .body("data.expiresIn", notNullValue())
        .body("data.refreshExpiresIn", notNullValue());
  }

  @Test
  void signUp_ValidRequest_CreatesUserAndReturnsOk() throws MessagingException {
    String email = faker.internet().emailAddress();
    patientRegistrationRequest.setEmail(email);

    // DEVNOTE: We mock the email service to avoid sending actual emails.
    doNothing().when(emailVerificationService).sendUserVerificationEmail(any(User.class));

    if (userRepository.findByEmail(patientRegistrationRequest.getEmail()).isPresent()) {
      throw new AssertionFailedError("User is already presented in db");
    }

    given()
        .contentType(ContentType.JSON)
        .body(patientRegistrationRequest)
        .when()
        .post("/auth/signup")
        .then()
        .statusCode(200);

    if (userRepository.findByEmail(patientRegistrationRequest.getEmail()).isEmpty()) {
      throw new AssertionFailedError("User was not created");
    }
  }

  @Test
  void refreshToken_ValidRequest_ReturnsJwtTokenAndReturnsOk() {
    RefreshToken refreshToken = refreshTokenProvider.provide(List.of(user));
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken(refreshToken.getToken());

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/refresh")
        .then()
        .statusCode(200)
        .body("data.accessToken", notNullValue())
        .body("data.refreshToken", equalTo(refreshToken.getToken()))
        .body("data.expiresIn", notNullValue())
        .body("data.refreshExpiresIn", notNullValue());
  }

  @Test
  void verifyUser_ValidRequest_VerifiesUserAndReturnsOk() throws MessagingException {
    String emailToVerify = faker.internet().emailAddress();
    patientRegistrationRequest.setEmail(emailToVerify);
    patientRegistrationRequest.setPhone(faker.phoneNumber().subscriberNumber(11));

    // DEVNOTE: We mock the email service to avoid sending actual emails.
    doNothing().when(emailVerificationService).sendUserVerificationEmail(any(User.class));

    given()
        .contentType(ContentType.JSON)
        .body(patientRegistrationRequest)
        .when()
        .post("/auth/signup");

    VerificationToken verificationToken = verificationTokenRepository.findByUserEmail(emailToVerify)
        .orElseThrow(
            () -> new AssertionFailedError("Verification token does not exist")
        );
    String token = verificationToken.getToken();

    if (userRepository.findByEmail(emailToVerify).isEmpty() || userRepository.findByEmail(
        emailToVerify).get().getEnabled()) {
      throw new AssertionFailedError("User is already verified");
    }

    given()
        .param("email", emailToVerify)
        .param("code", token)
        .when()
        .post("/auth/verify")
        .then()
        .statusCode(200);

    if (userRepository.findByEmail(emailToVerify).isEmpty() || !userRepository.findByEmail(
        emailToVerify).get().getEnabled()) {
      throw new AssertionFailedError("User was not verified");
    }
  }

  @Test
  void reverifyUser_ValidRequest_CreatedNewVerificationTokenAndReturnsOk()
      throws MessagingException {
    User user = userProvider.provide(List.of(patientProvider.provide()));
    user.setEnabled(false);
    userRepository.save(user);

    if (verificationTokenRepository.findByUserEmail(user.getEmail()).isPresent()) {
      throw new AssertionFailedError("Verification token exists for user created by provider");
    }

    // DEVNOTE: We mock the email service to avoid sending actual emails.
    doNothing().when(emailVerificationService).sendUserVerificationEmail(any(User.class));

    given()
        .param("email", user.getEmail())
        .when()
        .post("/auth/reverify")
        .then()
        .statusCode(200);

    if (verificationTokenRepository.findByUserEmail(user.getEmail()).isEmpty()) {
      throw new AssertionFailedError("Verification token was not created one more time");
    }
  }
}
