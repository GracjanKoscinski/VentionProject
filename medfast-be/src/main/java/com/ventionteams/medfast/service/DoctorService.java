package com.ventionteams.medfast.service;

import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for the appointment entity.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

  private final DoctorRepository repository;
  private final HospitalServiceService hospitalServiceService;
  private final TimeSlotService timeSlotService;

  /**
   * Checks if doctor provides specific service.
   */
  public boolean isProviding(Doctor doctor, HospitalService service) {
    return repository.existsBySpecializationsServicesAndId(service, doctor.getId());
  }

  public Doctor findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(Doctor.class, id));
  }

  /**
   * Provides the doctors for the given service.
   */
  public List<Doctor> findDoctorsByServiceAndFullName(
      Long serviceId, Optional<String> fullName) {
    List<Doctor> doctors;
    if (fullName.isPresent() && !fullName.get().isEmpty()) {
      doctors = repository.findBySpecializationsServiceIdAndFullName(serviceId, fullName.get());
    } else {
      doctors = repository.findBySpecializationsServicesId(serviceId);
    }
    return doctors;
  }

  /**
   * Provides a list of occupied time slots for the logged patient and provided doctor for the
   * month.
   */
  public List<TimeSlotResponse> getOccupiedTimeSlotsForPatientAndDoctor(User authenticatedUser,
      Long doctorId, Long serviceId, Integer month, Integer year) {
    HospitalService service = hospitalServiceService.findById(serviceId);
    Doctor doctor = this.findById(doctorId);

    return timeSlotService.getOccupiedTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor, service, month, year);
  }

  public boolean isWorkingInLocation(Doctor doctor, Long locationId) {
    return Objects.equals(doctor.getLocation().getId(), locationId);
  }

  /**
   * Provides a list of available time slots for the logged patient and provided doctor for the
   * month.
   */
  public List<TimeSlot> getAvailableTimeSlotsForPatientAndDoctor(User authenticatedUser,
      Long doctorId, Long serviceId, Long locationId, Integer month, Integer year) {
    HospitalService service = hospitalServiceService.findById(serviceId);
    Doctor doctor = this.findById(doctorId);

    return timeSlotService.getAvailableTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor, service, locationId, month, year);
  }
}
