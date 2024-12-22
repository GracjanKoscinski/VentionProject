package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.repository.SpecializationRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks specialization service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class SpecializationServiceTests {

  @Mock
  private SpecializationRepository repository;

  @InjectMocks
  private SpecializationService specializationService;

  @Test
  void getListOfMissingSpecializations_AllFound_ReturnsEmptyList() {
    List<Specialization> foundSpecializations = Arrays.asList(
        new Specialization(1L, "Cardiology", null, null),
        new Specialization(2L, "Neurology", null, null)
    );
    List<Long> requestedIds = Arrays.asList(1L, 2L);

    List<Long> result = specializationService
        .getListOfMissingSpecializations(foundSpecializations, requestedIds);

    assertEquals(Collections.emptyList(), result);
  }

  @Test
  void getListOfMissingSpecializations_SomeMissing_ReturnsMissingIds() {
    List<Specialization> foundSpecializations = Collections.singletonList(
        new Specialization(1L, "Cardiology", null, null)
    );
    List<Long> requestedIds = Arrays.asList(1L, 2L);

    List<Long> result = specializationService
        .getListOfMissingSpecializations(foundSpecializations, requestedIds);

    assertEquals(Collections.singletonList(2L), result);
  }

  @Test
  void getListOfMissingSpecializations_NoneFound_ReturnsAllIds() {
    List<Specialization> foundSpecializations = Collections.emptyList();
    List<Long> requestedIds = Arrays.asList(1L, 2L);

    List<Long> result = specializationService
        .getListOfMissingSpecializations(foundSpecializations, requestedIds);

    assertEquals(requestedIds, result);
  }

  @Test
  void findAllById_ReturnsSpecializations() {
    List<Long> specializationIds = Arrays.asList(1L, 2L);
    List<Specialization> specializations = Arrays.asList(
        new Specialization(1L, "Cardiology", null, null),
        new Specialization(2L, "Neurology", null, null)
    );

    when(repository.findAllById(specializationIds)).thenReturn(specializations);

    List<Specialization> result = specializationService.findAllById(specializationIds);

    assertEquals(specializations, result);
  }

  @Test
  void findAllById_EmptyList_ReturnsEmptyList() {
    List<Long> specializationIds = Collections.emptyList();
    List<Specialization> specializations = Collections.emptyList();

    when(repository.findAllById(specializationIds)).thenReturn(specializations);

    List<Specialization> result = specializationService.findAllById(specializationIds);

    assertEquals(specializations, result);
  }
}
