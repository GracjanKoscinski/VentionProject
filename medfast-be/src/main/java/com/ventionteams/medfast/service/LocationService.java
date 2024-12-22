package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.repository.LocationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for available locations for the appointment entity.
 */
@Service
@RequiredArgsConstructor
public class LocationService {

  private final LocationRepository locationRepository;
  private final HospitalServiceService hospitalServiceService;
  private final AppointmentRepository appointmentRepository;
  private final MedicalTestService medicalTestService;

  /**
   * Returns list of all available locations.
   */
  public List<Location> getAvailableLocations(
      Long serviceId, Long doctorId, LocalDateTime dateTime) {
    HospitalService service = hospitalServiceService.findById(serviceId);

    List<Location> locations = locationRepository
        .findByDoctorsSpecializationsServicesAndDoctorsId(service, doctorId);

    if (dateTime == null) {
      return locations;
    }

    return locations.stream()
        .filter(location -> isLocationAvailable(location, doctorId, dateTime))
        .collect(Collectors.toList());
  }

  /**
   * Helper for checking if a specific location is available for a given doctor and date.
   */
  private boolean isLocationAvailable(Location location, Long doctorId, LocalDateTime dateTime) {
    List<ConsultationAppointment> appointments = appointmentRepository
        .findByLocationIdAndDoctorId(location.getId(), doctorId);

    return appointments.stream()
        .noneMatch(appointment -> !dateTime.isBefore(appointment.getDateFrom())
            && !dateTime.isAfter(appointment.getDateTo()));
  }

  /**
   * Finds location by id.
   */
  public Location findById(Long locationId) {
    return locationRepository.findById(locationId).orElseThrow(() ->
        new EntityNotFoundException(Location.class, locationId)
    );
  }

  /**
   * Finds location by id.
   */
  public Location findByIdAndMedicalTest(Long locationId, MedicalTest test) {
    return locationRepository.findByIdAndTestsId(locationId, test.getId()).orElseThrow(() ->
        new EntityNotFoundException(Location.class, locationId)
    );
  }

  /**
   * Retrieves a list of distinct locations where a specific medical test can be scheduled.
   */
  public List<Location> getAvailableLocationsByTestId(Long testId) {
    medicalTestService.findById(testId);
    return locationRepository.findAllByTestsIdOrderByHospitalNameAsc(testId);
  }
}
