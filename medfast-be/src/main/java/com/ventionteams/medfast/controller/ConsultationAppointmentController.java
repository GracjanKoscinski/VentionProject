package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.RescheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.mapper.appointments.AppointmentsToDoctorSummaryResponseMapper;
import com.ventionteams.medfast.mapper.appointments.AppointmentsToResponseMapper;
import com.ventionteams.medfast.service.appointment.ConsultationAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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
 * Appointment controller that handles the appointment requests.
 */
@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient")
@Tag(name = "Appointment Controller", description = "Operations related to appointments")
public class ConsultationAppointmentController {

  private final AppointmentsToDoctorSummaryResponseMapper appointmentsToDoctorSummaryResponseMapper;
  private final AppointmentsToResponseMapper appointmentsToResponseMapper;
  private final ConsultationAppointmentService appointmentService;

  /**
   * Provides the appointments for the logged in patient.
   */
  @Operation(summary = "Request the list of patients' appointments")
  @GetMapping("/appointments")
  public ResponseEntity<StandardizedResponse<List<AppointmentResponse>>> getAppointments(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "type") AppointmentRequestType type) {

    StandardizedResponse<List<AppointmentResponse>> response;

    Optional<Person> authenticatedPerson = Optional.ofNullable(authenticatedUser.getPerson());

    List<AppointmentResponse> appointments =
        appointmentService.getAppointments(authenticatedPerson, amount, type);

    response = StandardizedResponse.ok(
        appointments,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides the list of patients' care team.
   */
  @Operation(summary = "Request the list of patients' care team")
  @GetMapping("/care-team")
  public ResponseEntity<StandardizedResponse<List<DoctorSummaryResponse>>> getCareTeam(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "elementSelection", required = false)
      Optional<ElementSelection> selection) {

    List<ConsultationAppointment> consultationAppointments =
        appointmentService.getConsultationAppointments(authenticatedUser, selection);

    List<DoctorSummaryResponse> doctorSummaryResponse = 
        appointmentsToDoctorSummaryResponseMapper.apply(consultationAppointments);

    StandardizedResponse<List<DoctorSummaryResponse>> response = StandardizedResponse.ok(
          doctorSummaryResponse,
          HttpStatus.OK.value(),
          "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Schedules a new consultation appointment for the logged-in patient.
   */
  @Operation(summary = "Schedules a new appointment")
  @PostMapping("/appointments")
  public ResponseEntity<StandardizedResponse<Long>> scheduleAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid ScheduleConsultationAppointmentRequest request)
      throws MessagingException {

    ConsultationAppointment consultationAppointment =
        appointmentService.scheduleConsultationAppointment(request, authenticatedUser);

    StandardizedResponse<Long> response =
        StandardizedResponse.ok(
            consultationAppointment.getId(),
            HttpStatus.CREATED.value(),
            "Operation successful");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Cancels consultation appointment for the logged-in patient.
   */
  @Operation(summary = "Cancels consultation appointment for the logged-in patient.")
  @PutMapping("/appointments/{appointmentId}/cancel")
  public ResponseEntity<StandardizedResponse<Long>> cancelAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @PathVariable Long appointmentId) throws MessagingException {

    appointmentService.cancelConsultationAppointment(appointmentId, authenticatedUser);

    StandardizedResponse<Long> response =
        StandardizedResponse.ok(
            null,
            HttpStatus.NO_CONTENT.value(),
            "Operation successful");
    log.info("Consultation appointment successfully cancelled for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Get appointment for a patient if he owns it.
   */
  @Operation(summary = "Get appointment")
  @GetMapping("/appointments/{appointmentId}")
  public ResponseEntity<StandardizedResponse<AppointmentResponse>> getAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @PathVariable(name = "appointmentId") Long appointmentId) {

    AppointmentResponse appointment = appointmentsToResponseMapper.apply(
        List.of(
            appointmentService.getAppointment(appointmentId, authenticatedUser)
        )).get(0);

    StandardizedResponse<AppointmentResponse> response =
        StandardizedResponse.ok(
            appointment,
            HttpStatus.OK.value(),
            "Operation successful");
    log.info("Consultation appointment successfully acquired for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Reschedules consultation appointment for the logged-in patient.
   */
  @Operation(summary = "Reschedules consultation appointment for the logged-in patient.")
  @PutMapping("/appointments/{appointmentId}/reschedule")
  public ResponseEntity<StandardizedResponse<Void>> rescheduleAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @Valid @RequestBody RescheduleConsultationAppointmentRequest request)
      throws MessagingException {

    appointmentService.rescheduleAppointment(request, authenticatedUser);

    StandardizedResponse<Void> response =
        StandardizedResponse.ok(
            null,
            HttpStatus.NO_CONTENT.value(),
            "Operation successful");
    log.info("Consultation appointment successfully rescheduled for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
