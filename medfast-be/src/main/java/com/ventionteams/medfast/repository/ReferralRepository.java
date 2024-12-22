package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Referral;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the Referral entity.
 */
public interface ReferralRepository extends JpaRepository<Referral, Long> {

  List<Referral> findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
      Patient patient, LocalDate currentDate, Pageable pageable);

  List<Referral> findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
      Patient patient, LocalDate currentDate, Pageable pageable);
}
