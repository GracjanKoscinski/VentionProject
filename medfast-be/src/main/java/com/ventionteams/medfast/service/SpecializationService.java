package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.repository.SpecializationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for the Specialization entity.
 */
@Service
@RequiredArgsConstructor
public class SpecializationService {

  private final SpecializationRepository repository;

  /**
   * Returns a list of ids of specializations which were not found.
   */
  public List<Long> getListOfMissingSpecializations(
      List<Specialization> foundSpecializations,
      List<Long> requestedSpecializationIds
  ) {
    List<Long> foundSpecializationIds = foundSpecializations.stream()
        .map(Specialization::getId).toList();
    return requestedSpecializationIds.stream()
        .filter(id -> !foundSpecializationIds.contains(id))
        .toList();
  }

  public List<Specialization> findAllById(List<Long> specializationIds) {
    return repository.findAllById(specializationIds);
  }
}
