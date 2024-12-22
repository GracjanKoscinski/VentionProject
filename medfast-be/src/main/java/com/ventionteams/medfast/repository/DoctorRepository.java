package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for the doctor entity.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long>,
    JpaSpecificationExecutor<Doctor> {

  List<Doctor> findAllByOrderBySurnameAsc(Pageable pageable);

  List<Doctor> findAllByOrderByUserEmailAsc(Pageable pageable);

  List<Doctor> findAllByOrderBySpecializationsSpecializationAsc(Pageable pageable);

  List<Doctor> findAllByOrderByUserUserStatusAsc(Pageable pageable);

  List<Doctor> findBySpecializationsServicesId(Long serviceId);

  @Query("SELECT d FROM Doctor d JOIN d.specializations s JOIN s.services serv "
      + "WHERE serv.id = :serviceId "
      + "AND CONCAT(d.name, ' ', d.surname) ILIKE %:fullName%")
  List<Doctor> findBySpecializationsServiceIdAndFullName(Long serviceId, String fullName);

  boolean existsBySpecializationsServicesAndId(HospitalService service, Long doctorId);
}
