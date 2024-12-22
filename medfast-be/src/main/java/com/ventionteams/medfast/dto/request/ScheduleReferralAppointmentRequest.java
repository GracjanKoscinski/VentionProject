package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create a schedule referral appointment request transfer object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Create a referral request that includes appointment details")
public class ScheduleReferralAppointmentRequest extends ScheduleConsultationAppointmentRequest {
  
  @Schema(description = "Referral id", example = "45")
  @NotNull
  private Long referralId;

  /**
   * Method to convert from ScheduleReferralAppointmentRequest.
   */
  public static ScheduleConsultationAppointmentRequest from(
      ScheduleReferralAppointmentRequest referralRequest) {
    return new ScheduleConsultationAppointmentRequest(
        referralRequest.getDoctorId(),
        referralRequest.getLocationId(),
        referralRequest.getServiceId(),
        referralRequest.getDateFrom()
    );
  }
}

