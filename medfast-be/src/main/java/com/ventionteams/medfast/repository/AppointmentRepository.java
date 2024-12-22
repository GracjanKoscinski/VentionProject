package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for the Appointment entity.
 */
public interface AppointmentRepository extends JpaRepository<ConsultationAppointment, Long> {

  @Query("SELECT c FROM ConsultationAppointment c "
      + "WHERE c.doctor = :person OR c.patient = :person "
      + "ORDER BY c.dateFrom ASC")
  List<ConsultationAppointment> findAllByPatientOrDoctorOrderByDateFromAsc(
      @Param("person") Person person);

  @Query(value = "SELECT * FROM consultation_appointments c "
      + "WHERE c.patient_id = :patientId AND c.appointment_status = :status "
      + "ORDER BY c.date_from DESC OFFSET :offset LIMIT :limit", nativeQuery = true)
  List<ConsultationAppointment> findByStatusOrderByDateFromDesc(
      @Param("patientId") Long patientId,
      @Param("status") String status,
      @Param("offset") int offset,
      @Param("limit") int limit);

  @Query("SELECT c FROM ConsultationAppointment c "
      + "WHERE (c.patient = :person OR c.doctor = :person)"
      + "AND c.status NOT IN :statuses "
      + "AND c.dateFrom BETWEEN :startDate AND :endDate "
      + "ORDER BY c.dateFrom ASC")
  List<ConsultationAppointment> findByPatientOrDoctorAndStatusNotInAndStartEndDate(
      @Param("person") Person person,
      @Param("statuses") List<AppointmentStatus> statuses,
      @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);

  @Query("SELECT a FROM ConsultationAppointment a WHERE "
        + "(a.patient = :person OR a.doctor = :person) "
        + "AND a.location.id = :locationId "
        + "AND a.status NOT IN :excludedStatuses "
        + "AND a.dateFrom >= :startOfMonth AND a.dateTo <= :endOfMonth")
  List<ConsultationAppointment> findByPatientOrDoctorAndLocationAndStatusNotInAndStartEndDate(
        @Param("person") Person person,
        @Param("locationId") Long locationId,
        @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses,
        @Param("startOfMonth") LocalDateTime startOfMonth,
        @Param("endOfMonth") LocalDateTime endOfMonth);

  List<ConsultationAppointment> findByLocationIdAndDoctorId(Long id, Long doctorId);

  List<ConsultationAppointment> findByDoctor(Doctor doctor);

  List<ConsultationAppointment> findByPatient(Patient patient);

  List<ConsultationAppointment> findByDoctorAndStatusNotIn(Doctor doctor,
      List<AppointmentStatus> excludedStatuses);

  List<ConsultationAppointment> findByPatientAndStatusNotIn(Patient patient,
      List<AppointmentStatus> excludedStatuses);
}
