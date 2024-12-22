package com.ventionteams.medfast.mapper.doctor;

import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.service.filesystem.ProfilePictureService;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts doctor to doctor summary.
 */
@Component
@RequiredArgsConstructor
public class DoctorToDoctorSummaryResponseMapper implements Function<Doctor,
    DoctorSummaryResponse> {

  private final ProfilePictureService profilePictureService;

  @Override
  public DoctorSummaryResponse apply(Doctor doctor) {
    return DoctorSummaryResponse.builder()
        .id(doctor.getId())
        .specializations(doctor.getSpecializations().stream().map(
            Specialization::getSpecialization).toList())
        .userPhotoResponse(profilePictureService
            .getProfilePicture(doctor.getUser()))
        .fullName(doctor.getName() + " " + doctor.getSurname())
        .build();
  }
}
