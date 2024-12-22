package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Referral;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.repository.ReferralRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a referral entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class ReferralProvider implements EntityProvider<Referral> {

  private final ReferralRepository referralRepository;
  private final Random random;

  /**
   * Provides a referral entity.
   *
   * @param references it is list of next entities: Doctor, Specialization, Patient,
   *                   ConsultationAppointment, AppointmentRequestType;
   * @return Referral
   */
  @Override
  public Referral provide(List<Object> references) {
    Referral referral = new Referral();
    referral.setDateOfIssue(LocalDate.now().minusDays(
        random.nextInt(365)));
    referral.setExpirationDate(LocalDate.now().plusDays(10));
    references.forEach(reference -> {
      if (reference instanceof Doctor doctor) {
        referral.setDoctor(doctor);
      } else if (reference instanceof Specialization specialization) {
        referral.setSpecialization(specialization);
      } else if (reference instanceof Patient patient) {
        referral.setPatient(patient);
      } else if (reference instanceof ConsultationAppointment appointment) {
        referral.setConsultationAppointment(appointment);
      } else if (reference instanceof AppointmentRequestType type) {
        referral.setExpirationDate(type == AppointmentRequestType.PAST
            ? LocalDate.now().minusDays(5)
            : LocalDate.now().plusDays(5));
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the ReferralProvider",
            reference,
            "reference");
      }
    });

    return referralRepository.save(referral);
  }
}
