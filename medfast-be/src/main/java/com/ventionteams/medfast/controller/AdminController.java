package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorListResponse;
import com.ventionteams.medfast.enums.DoctorFilterByRequest;
import com.ventionteams.medfast.enums.DoctorSortByRequest;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller with endpoints which can be accessed only with admin account.
 */
@Log4j2
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/admin-console")
@Tag(name = "Admin Controller")
public class AdminController {

  private final AdminService adminService;

  /**
   * Registering a doctor.
   */
  @Operation(summary = "Register new doctor")
  @PostMapping("/registerDoctor")
  public ResponseEntity<StandardizedResponse<Void>> registerDoctor(
      @RequestBody @Valid DoctorRegistrationRequest request) throws MessagingException {

    StandardizedResponse<Void> response;
    adminService.registerDoctor(request);

    response = StandardizedResponse.ok(
        null,
        HttpStatus.CREATED.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Get list of doctors.
   */
  @Operation(summary = "Get list of doctors")
  @GetMapping("/get-doctors")
  public ResponseEntity<StandardizedResponse<DoctorListResponse>>  getDoctors(
      @RequestParam(name = "page", required = false) Optional<Integer> page,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "sort-by", required = false) Optional<DoctorSortByRequest> sortBy
  ) {
    StandardizedResponse<DoctorListResponse> response;
    DoctorListResponse doctors = adminService.getDoctors(page, amount, sortBy);
    response = StandardizedResponse.ok(
        doctors,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Get list of searched doctors.
   */
  @Operation(summary = "Get list of searched doctors")
  @GetMapping("/search-doctors")
  public ResponseEntity<StandardizedResponse<DoctorListResponse>>  getDoctors(
      @RequestParam(name = "page", required = false) Optional<Integer> page,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "sort-by", required = false) Optional<DoctorSortByRequest> sortBy,
      @RequestParam(name = "filter-by", required = false) Optional<DoctorFilterByRequest> filterBy,
      @RequestParam(name = "keyword") String keyword
  ) {
    StandardizedResponse<DoctorListResponse> response;
    DoctorListResponse doctors = adminService
        .searchDoctors(page, amount, sortBy, filterBy, keyword);
    response = StandardizedResponse.ok(
        doctors,
        HttpStatus.OK.value(),
        "Operation successful");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Deactivates user.
   */
  @Operation(summary = "Deactivates user.")
  @PutMapping("/deactivate")
  public ResponseEntity<StandardizedResponse<Void>> deactivateUser(
      @RequestParam(name = "user-email") String email
  ) {
    StandardizedResponse<Void> response;
    adminService.deactivateUser(email, UserStatus.DEACTIVATED);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Operation successful");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Deletes user.
   */
  @Operation(summary = "Deactivates user.")
  @PutMapping("/delete")
  public ResponseEntity<StandardizedResponse<Void>> deleteUser(
      @RequestParam(name = "user-email") String email
  ) {
    StandardizedResponse<Void> response;
    adminService.deactivateUser(email, UserStatus.DELETED);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Operation successful");
    return ResponseEntity.status(response.getStatus()).body(response);
  }


  /**
   * Activates user.
   */
  @Operation(summary = "Activates user.")
  @PutMapping("/activate")
  public ResponseEntity<StandardizedResponse<Void>> activateUser(
      @RequestParam(name = "user-email") String email
  ) {
    StandardizedResponse<Void> response;
    adminService.activateUser(email);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Operation successful");
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
