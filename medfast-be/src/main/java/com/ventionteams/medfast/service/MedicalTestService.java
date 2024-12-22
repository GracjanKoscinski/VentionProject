package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.MedicalTestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Medical test service responsible operations related to medical tests.
 */
@Service
@RequiredArgsConstructor
public class MedicalTestService {

  private final MedicalTestRepository medicalTestRepository;

  /**
   * Finds medical tests by keyword or all medical tests if there is no keyword.
   */
  public List<MedicalTest> findByKeyword(Optional<String> keyword) {
    if (keyword.isPresent() && !keyword.get().isEmpty()) {
      return medicalTestRepository.findByTestContainingIgnoreCase(keyword.get());
    } else {
      return medicalTestRepository.findAllByOrderByTestAsc();
    }
  }

  public MedicalTest findById(Long id) {
    return medicalTestRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(MedicalTest.class, id));
  }

  public Long getTestDuration(Long testId) {
    MedicalTest test = findById(testId);
    return test.getDuration();
  }
}
