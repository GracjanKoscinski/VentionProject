package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.ReferralResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Referral;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of referrals to a list of referral responses.
 */
@Component
public class ReferralsToResponseMapper implements
    Function<List<Referral>, List<ReferralResponse>> {

  @Override
  public List<ReferralResponse> apply(List<Referral> referrals) {
    return referrals.stream().map(referral -> {
      ConsultationAppointment appointment = referral.getConsultationAppointment();
      Doctor doctor = referral.getDoctor();

      return ReferralResponse.builder()
          .id(referral.getId())
          .appointmentId(
              appointment == null ? null : appointment.getId()
          )
          .appointmentStatus(
              appointment == null ? null : appointment.getStatus().toString()
          )
          .dateOfIssue(referral.getDateOfIssue())
          .expirationDate(referral.getExpirationDate())
          .issuedBy(doctor.getName() + " " + doctor.getSurname())
          .specialization(referral.getSpecialization().getSpecialization())
          .build();
    }).toList();
  }
}
