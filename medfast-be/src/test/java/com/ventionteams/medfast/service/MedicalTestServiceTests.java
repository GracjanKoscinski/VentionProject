package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.MedicalTestRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the medical test service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class MedicalTestServiceTests {

  @Mock
  private MedicalTestRepository medicalTestRepository;
  @InjectMocks
  private MedicalTestService medicalTestService;

  @Test
  public void findById_InvalidId_ExceptionThrown() {
    Long testId = 3L;
    when(medicalTestRepository.findById(testId)).thenThrow(
        new EntityNotFoundException(MedicalTest.class, testId)
    );
    assertThrows(EntityNotFoundException.class, () ->
        medicalTestService.findById(testId));
  }

  @Test
  public void findById_ValidId_ReturnsTest() {
    Long testId = 3L;
    when(medicalTestRepository.findById(testId)).thenReturn(Optional.of(new MedicalTest()));
    medicalTestService.findById(testId);
    verify(medicalTestRepository).findById(testId);
  }

  @Test
  public void findByKeyword_Keyword_ReturnsList() {
    String keyword = "rial";
    when(medicalTestRepository.findByTestContainingIgnoreCase(keyword))
        .thenReturn(List.of(new MedicalTest()));
    medicalTestService.findByKeyword(Optional.of(keyword));
    verify(medicalTestRepository).findByTestContainingIgnoreCase(keyword);
  }

  @Test
  public void findByKeyword_NoKeyword_ReturnsList() {
    when(medicalTestRepository.findAllByOrderByTestAsc())
        .thenReturn(List.of());
    medicalTestService.findByKeyword(Optional.empty());
    verify(medicalTestRepository).findAllByOrderByTestAsc();
  }

  @Test
  public void getTestDuration_ValidId_ReturnsDuration() {
    Long testId = 3L;
    Long expectedDuration = 30L;

    MedicalTest mockMedicalTest = new MedicalTest();
    mockMedicalTest.setDuration(expectedDuration);

    when(medicalTestRepository.findById(testId)).thenReturn(Optional.of(mockMedicalTest));

    Long actualDuration = medicalTestService.getTestDuration(testId);

    assertEquals(expectedDuration, actualDuration);
    verify(medicalTestRepository).findById(testId);
  }

  @Test
  public void getTestDuration_InvalidId_ExceptionThrown() {
    Long testId = 3L;

    when(medicalTestRepository.findById(testId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      medicalTestService.getTestDuration(testId);
    });
  }
}
