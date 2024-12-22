package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the patient entity.
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

}
