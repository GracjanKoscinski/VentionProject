package com.ventionteams.medfast.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.ventionteams.medfast.config.IntegrationTest;
import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.config.util.impl.UserProvider;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.userinfo.AddressInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.ContactInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.PersonalInfoRequest;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the user profile controller functionality with integration tests.
 */
@RequiredArgsConstructor
public class UserProfileControllerTest extends IntegrationTest {

  private final EntityProvider<Patient> patientProvider;
  private final EntityProvider<User> userProvider;

  String jwt;
  User userPatient;

  /**
   * Sets up the user and jwt token for the tests.
   */
  @BeforeEach
  public void setUp() {
    Patient patient = patientProvider.provide();
    userPatient = userProvider.provide(List.of(patient));

    SignInRequest request = new SignInRequest(userPatient.getEmail(),
        ((UserProvider) userProvider).getRawPassword(userPatient.getEmail()));

    Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/signin");

    jwt = response.body().jsonPath().getString("data.accessToken");
  }

  @Test
  public void savePhoto_ValidPhoto_SavesPhotoAndReturnsOk() {
    File photo = new File("src/main/resources/templates/logos/logo.png");
    given()
        .contentType("multipart/form-data")
        .multiPart("photo", photo, "image/png")
        .header("Authorization", "Bearer " + jwt)
        .when()
        .post("/api/user/profile/photo/upload")
        .then()
        .statusCode(200);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .delete("/api/user/profile/photo/delete");

  }

  @Test
  public void getPhoto_ValidRequest_ReturnsPhotoAndOkStatus() throws IOException {
    File photo = new File("src/main/resources/static/images/profile/default-profile-picture.png");
    given()
        .contentType("multipart/form-data")
        .multiPart("photo", photo, "image/png")
        .header("Authorization", "Bearer " + jwt)
        .when()
        .post("/api/user/profile/photo/upload");

    Response uploadResponse = given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/user/profile/photo/get")
        .then()
        .statusCode(200)
        .extract().response();

    byte[] returnedPhotoBytes = uploadResponse.getBody().asByteArray();
    byte[] originalPhotoBytes = Files.readAllBytes(photo.toPath());

    assertThat("The uploaded and returned photo should be the same",
        returnedPhotoBytes, equalTo(originalPhotoBytes));

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .delete("/api/user/profile/photo/delete");
  }

  @Test
  public void deletePhoto_ValidRequest_ReturnsOkStatus() {
    File photo = new File("src/main/resources/templates/logos/logo.png");
    given()
        .contentType("multipart/form-data")
        .multiPart("photo", photo, "image/png")
        .header("Authorization", "Bearer " + jwt)
        .when()
        .post("/api/user/profile/photo/upload");

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .delete("/api/user/profile/photo/delete")
        .then()
        .statusCode(200)
        .body("message", equalTo("User photo has been deleted"));

  }

  @Test
  public void getUserInfo_ValidRequest_ReturnsUserInfoAndOkStatus() {
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .when()
        .get("/api/user/profile/info")
        .then()
        .statusCode(200)
        .body("data.checkboxTermsAndConditions",
            equalTo(userPatient.isCheckboxTermsAndConditions()))
        .body("data.personalInfo.name", equalTo(userPatient.getPerson().getName()))
        .body("data.personalInfo.surname", equalTo(userPatient.getPerson().getSurname()))
        .body("data.personalInfo.birthDate",
            equalTo(userPatient.getPerson().getBirthDate().toString()))
        .body("data.personalInfo.sex", equalTo(userPatient.getPerson().getSex().name()))
        .body("data.personalInfo.citizenship", equalTo(userPatient.getPerson().getCitizenship()))
        .body("data.contactInfo.phone",
            equalTo(userPatient.getPerson().getContactInfo().getPhone()))
        .body("data.contactInfo.email",
            equalTo(userPatient.getEmail()))
        .body("data.addressInfo.streetAddress",
            equalTo(userPatient.getPerson().getAddress().getStreetAddress()))
        .body("data.addressInfo.city", equalTo(userPatient.getPerson().getAddress().getCity()))
        .body("data.addressInfo.zip", equalTo(userPatient.getPerson().getAddress().getZip()))
        .body("data.addressInfo.state", equalTo(userPatient.getPerson().getAddress().getState()))
        .body("data.addressInfo.apartment",
            equalTo(userPatient.getPerson().getAddress().getApartment()))
        .body("data.addressInfo.house", equalTo(userPatient.getPerson().getAddress().getHouse()));
  }

  @Test
  public void updateUserPersonalInfo_ValidRequest_ReturnsUserPersonalInfoAndOkStatus() {
    PersonalInfoRequest request = PersonalInfoRequest.builder()
        .name("TestName")
        .surname("TestSurname")
        .birthDate(LocalDate.of(1990, 6, 5))
        .sex(userPatient.getPerson().getSex())
        .citizenship(userPatient.getPerson().getCitizenship())
        .build();
    request.setBirthDate(LocalDate.of(1990, 6, 5));
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .put("/api/user/profile/personal-info")
        .then()
        .statusCode(200)
        .body("data.name", equalTo("TestName"))
        .body("data.surname", equalTo("TestSurname"))
        .body("data.birthDate",
            equalTo(LocalDate.of(1990, 6, 5).toString()))
        .body("data.sex", equalTo(userPatient.getPerson().getSex().name()))
        .body("data.citizenship", equalTo(userPatient.getPerson().getCitizenship()));
  }

  @Test
  public void updateUserAddressInfo_ValidRequest_ReturnsUserAddressInfoAndOkStatus() {
    AddressInfoRequest request = AddressInfoRequest.builder()
        .zip(userPatient.getPerson().getAddress().getZip())
        .state(userPatient.getPerson().getAddress().getState())
        .apartment(userPatient.getPerson().getAddress().getApartment())
        .house("TestHouse")
        .city("TestCity")
        .streetAddress("TestStreetAddress")
        .build();
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .put("/api/user/profile/address-info")
        .then()
        .statusCode(200)
        .body("data.streetAddress", equalTo("TestStreetAddress"))
        .body("data.city", equalTo("TestCity"))
        .body("data.zip", equalTo(userPatient.getPerson().getAddress().getZip()))
        .body("data.state", equalTo(userPatient.getPerson().getAddress().getState()))
        .body("data.apartment",
            equalTo(userPatient.getPerson().getAddress().getApartment()))
        .body("data.house", equalTo("TestHouse"));
  }

  @Test
  public void updateUserContactInfo_ValidRequest_ReturnsUserContactInfoAndOkStatus() {
    ContactInfoRequest request = new ContactInfoRequest();
    request.setPhone("15551234568");
    request.setEmail(userPatient.getEmail());
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + jwt)
        .body(request)
        .when()
        .put("/api/user/profile/contact-info")
        .then()
        .statusCode(200)
        .body("data.phone", equalTo("15551234568"));
  }
}
