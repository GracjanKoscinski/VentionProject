package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.userinfo.AddressInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.ContactInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.PersonalInfoRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.dto.response.userinfo.AddressInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.ContactInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.PersonalInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.UserInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.UserPhotoResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.EmailSendException;
import com.ventionteams.medfast.service.UserService;
import com.ventionteams.medfast.service.auth.AuthenticationService;
import com.ventionteams.medfast.service.filesystem.ProfilePictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * User profile Controller that handles the user details requests.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name = "User profile Controller", description = "Operations related to user profile")
@RequestMapping("/api/user/profile")
public class UserProfileController {

  private final UserService userService;
  private final AuthenticationService authenticationService;
  private final ProfilePictureService profilePictureService;

  /**
   * Saves profile picture for the logged-in user or updates it if it already exists.
   */
  @Operation(summary = "Upload user profile picture")
  @PostMapping("/photo/upload")
  public ResponseEntity<StandardizedResponse<Void>> savePhoto(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam("photo") MultipartFile photo) {
    profilePictureService.saveProfilePicture(authenticatedUser, photo);
    StandardizedResponse<Void> response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "User photo has been uploaded successfully");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides profile picture for the logged-in user.
   */
  @Operation(summary = "Get user profile picture")
  @GetMapping("/photo/get")
  public ResponseEntity<ByteArrayResource> getPhoto(
      @AuthenticationPrincipal User authenticatedUser) {
    UserPhotoResponse userPhoto = profilePictureService.getProfilePicture(authenticatedUser);
    ByteArrayResource resource = new ByteArrayResource(userPhoto.getUserPhoto());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(userPhoto.getContentType()))
        .body(resource);
  }

  /**
   * Deletes profile picture for the logged-in user.
   */
  @Operation(summary = "Delete user profile picture")
  @DeleteMapping("/photo/delete")
  public ResponseEntity<StandardizedResponse<Void>> deletePhoto(
      @AuthenticationPrincipal User authenticatedUser) {
    StandardizedResponse<Void> response;
    profilePictureService.deleteProfilePicture(authenticatedUser);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "User photo has been deleted");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides user's info.
   */
  @Operation(summary = "Get user info")
  @GetMapping("/info")
  public ResponseEntity<StandardizedResponse<UserInfoResponse>> getUserInfo(
      @AuthenticationPrincipal User authenticatedUser) {
    StandardizedResponse<UserInfoResponse> response;
    UserInfoResponse userInfo = userService.getUserInfo(authenticatedUser);
    response = StandardizedResponse.ok(
        userInfo,
        HttpStatus.OK.value(),
        "User info has been provided");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Update user personal info.
   */
  @Operation(summary = "Update user personal info")
  @PutMapping("/personal-info")
  public ResponseEntity<StandardizedResponse<PersonalInfoResponse>> updateUserPersonalInfo(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid PersonalInfoRequest request) {
    StandardizedResponse<PersonalInfoResponse> response;
    PersonalInfoResponse personalInfo = userService.updatePersonalInfo(authenticatedUser, request);
    response = StandardizedResponse.ok(
        personalInfo,
        HttpStatus.OK.value(),
        "User personal info has been updated");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Update user contact info for not verified users.
   */
  @Operation(summary = "Update user contact info")
  @PutMapping("/contact-info")
  public ResponseEntity<StandardizedResponse<ContactInfoResponse>> updateUserContactInfo(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid ContactInfoRequest request) {
    StandardizedResponse<ContactInfoResponse> response;
    ContactInfoResponse personalInfo = userService.updateContactInfo(authenticatedUser, request);
    if (!Objects.equals(request.getEmail(), authenticatedUser.getEmail())) {
      try {
        personalInfo = authenticationService.changeUserEmail(authenticatedUser, request.getEmail());
      } catch (Exception e) {
        throw new EmailSendException();
      }
    }
    response = StandardizedResponse.ok(
        personalInfo,
        HttpStatus.OK.value(),
        "User contact info has been updated");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Update user address info.
   */
  @Operation(summary = "Update user address info")
  @PutMapping("/address-info")
  public ResponseEntity<StandardizedResponse<AddressInfoResponse>> updateUserAddressInfo(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid AddressInfoRequest request) {
    StandardizedResponse<AddressInfoResponse> response;
    AddressInfoResponse personalInfo = userService.updateAddressInfo(authenticatedUser, request);
    response = StandardizedResponse.ok(
        personalInfo,
        HttpStatus.OK.value(),
        "User address info has been updated");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

}