package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.enums.Gender;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for the service entity.
 */
public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {

  @Query("SELECT DISTINCT r.service FROM Recommendation r WHERE r.ageFrom <= :age AND "
      + "r.ageTo >= :age AND "
      + "((r.legalGender = 'NEUTRAL' AND (:legalGender = 'MALE' OR :legalGender = 'FEMALE')) OR "
      + "r.legalGender = :legalGender)")
  List<HospitalService> findServicesByAgeAndLegalGender(
      int age, Gender legalGender, Pageable pageable);
}
