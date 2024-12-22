package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for the MedicalTest entity.
 */
public interface MedicalTestAppointmentRepository
    extends JpaRepository<MedicalTestAppointment, Long>,
    JpaSpecificationExecutor<MedicalTestAppointment> {

  List<MedicalTestAppointment> findAllByPatientOrderByDateTimeDesc(Patient patient);

  @Modifying
  @Query("UPDATE MedicalTestAppointment t SET t.pdf = :pdf WHERE t.id = :id")
  void setPdfById(byte[] pdf, Long id);

  List<MedicalTestAppointment> findAllByDateTimeBetween(LocalDateTime start, LocalDateTime end);

  List<MedicalTestAppointment> findByPatient(Patient patient);

  List<MedicalTestAppointment> findByPatientAndStatusNotIn(Patient patient,
      List<AppointmentStatus> excludedStatuses);
}
