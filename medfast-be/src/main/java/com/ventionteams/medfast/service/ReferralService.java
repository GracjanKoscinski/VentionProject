package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Referral;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.ReferralRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service for the Referral entity.
 */
@Service
@RequiredArgsConstructor
public class ReferralService {

  private final ReferralRepository referralRepository;
  private final AppConfig appConfig;

  /**
   * Returns referrals for patient.
   */
  public List<Referral> getReferrals(User authenticatedUser, ElementSelection selection,
      AppointmentRequestType referralType) {
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    List<Referral> referrals;
    Pageable pageable;

    if (selection == ElementSelection.FIRST) {
      pageable = PageRequest.of(0, appConfig.elementCountLimit(),
          Sort.by("dateOfIssue").descending());
    } else {
      pageable = PageRequest.of(0, 1000, Sort.by("dateOfIssue").descending());
    }

    if (referralType.equals(AppointmentRequestType.UPCOMING)) {
      referrals = referralRepository
          .findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
              authenticatedPatient, LocalDate.now(), pageable);
    } else {
      referrals = referralRepository
          .findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
              authenticatedPatient, LocalDate.now(), pageable);
    }

    if (selection == ElementSelection.REMAINING) {
      referrals = referrals.stream().skip(appConfig.elementCountLimit()).toList();
    }

    return referrals;
  }

  /**
   * Assigns the specified consultation appointment to an existing referral.
   */
  public void assignConsultationAppointmentToReferral(
      Referral referral, ConsultationAppointment appointment) {
    referral.setConsultationAppointment(appointment);
    referralRepository.save(referral);
  }

  public Referral findById(Long id) {
    return referralRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(Referral.class, id));
  }
}
