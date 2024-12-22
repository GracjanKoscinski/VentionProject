package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the specialization entity.
 */
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

}
