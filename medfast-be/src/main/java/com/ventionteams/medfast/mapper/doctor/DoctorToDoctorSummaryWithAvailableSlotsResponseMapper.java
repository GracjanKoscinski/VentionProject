package com.ventionteams.medfast.mapper.doctor;

import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryWithAvailableSlotsResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.service.TimeSlotService;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import com.ventionteams.medfast.service.filesystem.ProfilePictureService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of doctors to a list of DoctorSummaryWithAvailableSlotsResponses.
 */
@Component
@RequiredArgsConstructor
public class DoctorToDoctorSummaryWithAvailableSlotsResponseMapper implements 
    BiFunction<List<Doctor>, HospitalService,
    List<DoctorSummaryWithAvailableSlotsResponse>> {

  private final TimeSlotService timeSlotService;
  private final ProfilePictureService profilePictureService;

  @Override
  public List<DoctorSummaryWithAvailableSlotsResponse> apply(
      List<Doctor> doctors, HospitalService service) {
    return doctors.stream()
        .map(doctor -> convertToDoctorSummaryWithAvailableSlotsResponse(doctor, service))
        .toList();
  }

  private DoctorSummaryWithAvailableSlotsResponse convertToDoctorSummaryWithAvailableSlotsResponse(
      Doctor doctor, HospitalService service) {
    return DoctorSummaryWithAvailableSlotsResponse.builder()
        .id(doctor.getId())
        .fullName(doctor.getName() + " " + doctor.getSurname())
        .specializations(doctor.getSpecializations().stream()
            .map(Specialization::getSpecialization)
            .toList())
        .availableSlots(
            getNumberOfAvailableSlotsForNextDays(doctor, service, 1))
        .userPhotoResponse(profilePictureService
            .getProfilePicture(doctor.getUser()))
        .build();
  }

  private Integer getNumberOfAvailableSlotsForNextDays(
      Doctor doctor, HospitalService service, int days) {
    LocalDateTime startDate = LocalDateTime.now();

    List<TimeSlot> occupiedTimeSlots = timeSlotService
        .getOccupiedTimeSlotsForMonth(doctor, service, startDate, null);

    List<TimeSlot> availableTimeSlots = Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(days)
        .flatMap(date -> timeSlotService.generateTimeSlotsForDay(service.getDuration(), date)
        .stream())
        .filter(timeSlot -> timeSlot.getStartTime().isAfter(startDate))
        .filter(timeSlot -> !occupiedTimeSlots.contains(timeSlot))
        .toList();

    return availableTimeSlots.size();
  }
}