package com.ventionteams.medfast.mapper.doctor;

import com.ventionteams.medfast.dto.response.adminconsole.DoctorResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Specialization;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of doctors to a list of doctorResponses.
 */
@Component
public class DoctorToResponseMapper implements Function<List<Doctor>,
                                         List<DoctorResponse>> {

  @Override
  public List<DoctorResponse> apply(List<Doctor> doctors) {
    return doctors.stream()
      .map(this::doctorToResponse)
      .collect(Collectors.toList());
  }

  /**
   * Converts a doctor to a doctor response.
   */
  public DoctorResponse doctorToResponse(Doctor doctor) {
    return DoctorResponse.builder()
      .name(doctor.getName().concat(" ").concat(doctor.getSurname()))
      .email(doctor.getUser().getEmail())
      .status(doctor.getUser().getUserStatus())
      .specializations(doctor.getSpecializations().stream()
          .map(Specialization::getSpecialization)
          .toList())
      .build();
  }
}
