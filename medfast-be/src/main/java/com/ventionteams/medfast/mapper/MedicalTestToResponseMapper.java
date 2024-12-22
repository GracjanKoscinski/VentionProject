package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.MedicalTestResponse;
import com.ventionteams.medfast.entity.MedicalTest;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of medical tests to a list of medical test responses.
 */
@Component
@RequiredArgsConstructor
public class MedicalTestToResponseMapper implements Function<List<MedicalTest>,
    List<MedicalTestResponse>> {

  private final LocationToResponseMapper locationToResponseMapper;

  @Override
  public List<MedicalTestResponse> apply(List<MedicalTest> medicalTests) {
    return medicalTests.stream()
                .map(this::convertToMedicalTestResponse)
                .toList();
  }

  /**
    * Converts a MedicalTest entity to a MedicalTestResponse DTO.
    *
    * @param medicalTest The MedicalTest entity
    */
  public MedicalTestResponse convertToMedicalTestResponse(MedicalTest medicalTest) {
    return MedicalTestResponse.builder()
                .id(medicalTest.getId())
                .test(medicalTest.getTest())
                .duration(medicalTest.getDuration())
                .locations(locationToResponseMapper.apply(medicalTest.getLocations()))
                .build();
  }
}
