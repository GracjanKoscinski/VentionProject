package com.ventionteams.medfast.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.HospitalServiceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

/**
 * Checks hospital service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class HospitalServiceServiceTests {

  @Mock
  private HospitalServiceRepository serviceRepository;

  @InjectMocks
  private HospitalServiceService hospitalServiceService;

  @Test
  void findById_InvalidServiceId_ExceptionThrown() {
    Long invalidId = 1L;

    when(serviceRepository.findById(invalidId)).thenReturn(Optional.empty());

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> hospitalServiceService.findById(invalidId));
  }

  @Test
  void getRecommendedServices_GoodRequest_ReturnList() {
    LocalDate birthDate = LocalDate.now().minusYears(30);
    Patient authenticatedPatient = Patient.builder()
        .birthDate(birthDate).sex(Gender.MALE).build();
    User authenticatedUser = User.builder().person(authenticatedPatient).build();
    List<HospitalService> expectedServices = List.of(new HospitalService());

    when(serviceRepository.findServicesByAgeAndLegalGender(
        eq(30), eq(Gender.MALE), any(Pageable.class))).thenReturn(expectedServices);

    List<HospitalService> recommendedServices =
        hospitalServiceService.getRecommendedServices(
            authenticatedUser, ElementSelection.FIRST);

    verify(serviceRepository).findServicesByAgeAndLegalGender(
        eq(30), eq(Gender.MALE), any(Pageable.class));
    Assertions.assertEquals(recommendedServices, expectedServices);
  }
}
