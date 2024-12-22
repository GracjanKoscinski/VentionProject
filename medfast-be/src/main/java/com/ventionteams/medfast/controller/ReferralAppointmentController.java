package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.ScheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleReferralAppointmentRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Referral;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.ReferralService;
import com.ventionteams.medfast.service.appointment.ConsultationAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Referral appointments controller that handles the referral and appointment requests.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/referral-appointments")
@Tag(name = "Referral appointment Controller", 
    description = "Operations related to referral appointments")
public class ReferralAppointmentController {

  private final ReferralService referralService;
  private final ConsultationAppointmentService appointmentService;

  /**
    * Creates a new appointment by referral based on the provided request data.
    */
  @Operation(summary = "Create a new appointment by referral")
  @PostMapping("/schedule")
  public ResponseEntity<StandardizedResponse<Long>> scheduleAppointment(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestBody @Valid ScheduleReferralAppointmentRequest request)
      throws MessagingException {

    ScheduleConsultationAppointmentRequest consultationAppointmentRequest = 
        ScheduleReferralAppointmentRequest.from(request);

    ConsultationAppointment consultationAppointment =
        appointmentService.scheduleConsultationAppointment(
          consultationAppointmentRequest, authenticatedUser);

    Referral referral = referralService.findById(request.getReferralId());
    referralService.assignConsultationAppointmentToReferral(referral, consultationAppointment);

    StandardizedResponse<Long> response =
        StandardizedResponse.ok(
            consultationAppointment.getId(),
            HttpStatus.CREATED.value(),
            "Operation successful");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
