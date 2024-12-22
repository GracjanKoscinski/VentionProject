package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.MedicalTest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the medical test entity.
 */
public interface MedicalTestRepository extends JpaRepository<MedicalTest, Long> {
  List<MedicalTest> findByTestContainingIgnoreCase(String keyword);

  List<MedicalTest> findAllByOrderByTestAsc();
}
