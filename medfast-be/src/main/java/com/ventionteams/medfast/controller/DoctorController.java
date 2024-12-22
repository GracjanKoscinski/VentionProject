package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryWithAvailableSlotsResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.mapper.TimeSlotsToResponseMapper;
import com.ventionteams.medfast.mapper.doctor.DoctorToDoctorSummaryWithAvailableSlotsResponseMapper;
import com.ventionteams.medfast.service.DoctorService;
import com.ventionteams.medfast.service.HospitalServiceService;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Doctor controller that handles the doctor requests.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
@Tag(name = "Doctor Controller", description = "Operations related to doctor")
public class DoctorController {

  private final DoctorService doctorService;
  private final HospitalServiceService hospitalServiceService;
  private final DoctorToDoctorSummaryWithAvailableSlotsResponseMapper
      doctorToDoctorSummaryWithAvailableSlotsResponseMapper;
  private final TimeSlotsToResponseMapper timeSlotsToResponseMapper;

  /**
   * Provides the list of occupied time slots for the logged in patient and provided doctor for the
   * month.
   */
  @Operation(summary = "Request the list of occupied time "
      + "slots for patient and doctor for the month")
  @GetMapping("/{doctorId}/appointments/occupied")
  public ResponseEntity<StandardizedResponse<List<TimeSlotResponse>>> getOccupiedTimeSlots(
      @AuthenticationPrincipal User authenticatedUser,
      @PathVariable(name = "doctorId") Long doctorId,
      @RequestParam(name = "serviceId") Long serviceId,
      @RequestParam(name = "month") Integer month,
      @RequestParam(name = "year") Integer year) {

    List<TimeSlotResponse> timeSlots = doctorService.getOccupiedTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctorId, serviceId, month, year);

    return ResponseEntity.ok(
        StandardizedResponse.ok(timeSlots, HttpStatus.OK.value(), "Operation successful"));
  }

  /**
   * Provides the doctors who provide specified service for the logged in patient.
   */
  @Operation(summary = "Request a list of doctors who provide the service")
  @GetMapping("/services/{serviceId}")
  public ResponseEntity<StandardizedResponse<List<DoctorSummaryWithAvailableSlotsResponse>>>
      getServiceDoctors(
      @PathVariable("serviceId") Long serviceId,
      @RequestParam(name = "fullName", required = false) Optional<String> fullName) {

    HospitalService service = hospitalServiceService.findById(serviceId);

    List<Doctor> doctors = doctorService
        .findDoctorsByServiceAndFullName(serviceId, fullName);

    List<DoctorSummaryWithAvailableSlotsResponse> doctorResponse =
        doctorToDoctorSummaryWithAvailableSlotsResponseMapper.apply(doctors, service);

    StandardizedResponse<List<DoctorSummaryWithAvailableSlotsResponse>> response =
        StandardizedResponse.ok(
            doctorResponse,
            HttpStatus.OK.value(),
            "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides the list of available time slots for the logged patient and provided doctor for the
   * month.
   */
  @Operation(summary = "Request the list of available time "
      + "slots for patient and doctor for the month")
  @GetMapping("/available-slots")
  public ResponseEntity<StandardizedResponse<List<TimeSlotResponse>>> getAvailableTimeSlots(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "doctorId") Long doctorId,
      @RequestParam(name = "serviceId") Long serviceId,
      @RequestParam(name = "locationId", required = false) Long locationId,
      @RequestParam(name = "month") Integer month,
      @RequestParam(name = "year") Integer year) {

    List<TimeSlot> timeSlots = doctorService.getAvailableTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctorId, serviceId, locationId, month, year);

    List<TimeSlotResponse> timeSlotsResponse = timeSlotsToResponseMapper.apply(timeSlots);

    StandardizedResponse<List<TimeSlotResponse>> response = StandardizedResponse.ok(
        timeSlotsResponse,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
