package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.RescheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.response.LocationResponse;
import com.ventionteams.medfast.dto.response.MedicalTestAppointmentResponse;
import com.ventionteams.medfast.dto.response.PdfResultResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.mapper.LocationToResponseMapper;
import com.ventionteams.medfast.mapper.MedicalTestAppointmentsToResponseMapper;
import com.ventionteams.medfast.mapper.TimeSlotsToResponseMapper;
import com.ventionteams.medfast.service.LocationService;
import com.ventionteams.medfast.service.TimeSlotService;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import com.ventionteams.medfast.service.appointment.MedicalTestAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Medical test appointments controller that handles the test requests.
 */
@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Medical test appointment Controller",
    description = "Operations related to medical test appointments")
@RequestMapping("/api/patient/test-appointments")
public class MedicalTestAppointmentController {

  private final MedicalTestAppointmentService medicalTestAppointmentService;
  private final TimeSlotsToResponseMapper timeSlotsToResponseMapper;
  private final MedicalTestAppointmentsToResponseMapper medicalTestsToResponseMapper;
  private final TimeSlotService timeSlotService;
  private final LocationToResponseMapper locationToResponseMapper;
  private final LocationService locationService;


  /**
   * Schedules a new medical test appointment for the logged-in patient.
   */
  @Operation(summary = "Creates a new medical test appointment for the logged-in patient")
  @PostMapping("/schedule")
  public ResponseEntity<StandardizedResponse<Long>> scheduleAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid ScheduleMedicalTestAppointmentRequest request) throws MessagingException {
    StandardizedResponse<Long> response;

    MedicalTestAppointment medicalTestAppointment = medicalTestAppointmentService
        .scheduleMedicalTestAppointment(request, authenticatedUser);
    response = StandardizedResponse.ok(
        medicalTestAppointment.getId(),
        HttpStatus.CREATED.value(),
        "Test has been created successfully");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides the tests for the logged-in user.
   */
  @Operation(summary = "Request the list of patient's tests")
  @GetMapping
  public ResponseEntity<StandardizedResponse<List<MedicalTestAppointmentResponse>>> getSortedTests(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "type") AppointmentRequestType type) {
    StandardizedResponse<List<MedicalTestAppointmentResponse>> response;

    List<MedicalTestAppointmentResponse> medicalTestsAppointments = 
        medicalTestsToResponseMapper.apply(
          medicalTestAppointmentService.getMedicalTests(user, amount, type));
    response = StandardizedResponse.ok(
        medicalTestsAppointments,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides PDF result of test appointment and generates it if it doesn't exist. Accessible for
   * the logged-in user.
   */
  @Operation(summary = "get test result")
  @GetMapping("/result")
  public ResponseEntity<Object> getTestResult(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "testId") Long testId) {

    PdfResultResponse pdf = medicalTestAppointmentService.getTestResult(authenticatedUser, testId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", pdf.getName());
    headers.setContentLength(pdf.getData().length);

    return new ResponseEntity<>(pdf.getData(), headers, HttpStatus.OK);
  }

  /**
   * Generates PDF result for the test appointment. Accessible only for admin.
   */
  @Operation(summary = "generate test result pdf for appointment")
  @PostMapping("/generate")
  public ResponseEntity<StandardizedResponse<String>> generateTestResultForAppointment(
      @RequestParam Long testAppointmentId) {
    StandardizedResponse<String> response;

    medicalTestAppointmentService.generateTestResultForAppointment(testAppointmentId);
    response = StandardizedResponse.ok(
        null,
        HttpStatus.OK.value(),
        "Test result has been generated successfully");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Retrieves a list of available time slots for a specific medical test based on the provided
   * filters. The result can be filtered by location, if specified.
   */
  @Operation(summary = "get a list of available time slots")
  @GetMapping("/available-timeslots")
  public ResponseEntity<StandardizedResponse<List<TimeSlotResponse>>> getAvailableTimeslots(
      @RequestParam Long testId,
      @RequestParam(required = false) Long locationId,
      @RequestParam(name = "month") Integer month,
      @RequestParam(name = "year") Integer year) {

    List<TimeSlot> timeSlotList = timeSlotService
        .getAvailableTimeSlotsForMedicalTests(testId, locationId, month, year);

    List<TimeSlotResponse> timeSlotResponses = timeSlotsToResponseMapper.apply(timeSlotList);

    StandardizedResponse<List<TimeSlotResponse>> response = StandardizedResponse.ok(
        timeSlotResponses,
        HttpStatus.OK.value(),
        "Operation successful"
    );

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Retrieves a list of available locations for a specific medical test.
   */
  @Operation(summary = "get a list of available locations")
  @GetMapping("/available-locations")
  public ResponseEntity<StandardizedResponse<List<LocationResponse>>> getAvailableLocations(
      @RequestParam Long testId) {

    List<Location> locations = locationService.getAvailableLocationsByTestId(testId);
    List<LocationResponse> locationResponse = locationToResponseMapper.apply(locations);

    StandardizedResponse<List<LocationResponse>> response = StandardizedResponse.ok(
        locationResponse,
        HttpStatus.OK.value(),
        "Operation successful"
    );

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Reschedules medical test appointment for the logged-in patient.
   */
  @Operation(summary = "Reschedules medical test appointment for the logged-in patient.")
  @PutMapping("/{appointmentId}/reschedule")
  public ResponseEntity<StandardizedResponse<Void>> rescheduleAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @Valid @RequestBody RescheduleMedicalTestAppointmentRequest request)
      throws MessagingException {

    medicalTestAppointmentService.rescheduleAppointment(request, authenticatedUser);

    StandardizedResponse<Void> response =
        StandardizedResponse.ok(
            null,
            HttpStatus.NO_CONTENT.value(),
            "Operation successful");
    log.info("Medical test appointment successfully rescheduled for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Cancels medical test appointment for the logged-in patient.
   */
  @Operation(summary = "Cancels medical test appointment for the logged-in patient")
  @PutMapping("/{appointmentId}/cancel")
  public ResponseEntity<StandardizedResponse<Long>> cancelAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @PathVariable Long appointmentId) throws MessagingException {

    medicalTestAppointmentService.cancelTestAppointment(appointmentId, authenticatedUser);

    StandardizedResponse<Long> response =
        StandardizedResponse.ok(
            null,
            HttpStatus.NO_CONTENT.value(),
            "Operation successful");

    log.info("Medical test appointment successfully cancelled for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}