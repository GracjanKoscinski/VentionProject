package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.repository.LocationRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the location service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {

  @Mock
  private LocationRepository locationRepository;

  @Mock
  private HospitalServiceService hospitalServiceService;

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private MedicalTestService medicalTestService;

  @InjectMocks
  private LocationService locationService;

  private static Doctor doctor;
  private static HospitalService service;
  private static LocalDateTime dateTime;
  private static Location location;
  private static ConsultationAppointment appointment;

  @BeforeEach
  void setUp() {
    dateTime = LocalDateTime.of(2024, 9, 18, 10, 0);
    doctor = new Doctor();
    service = new HospitalService();
    location = new Location();
    service = new HospitalService();
    Specialization specialization = new Specialization();
    specialization.setServices(List.of(service));
    doctor.setSpecializations(List.of(specialization));
  }

  @Test
  void getAvailableLocations_OverlappingAppointments_ReturnsEmptyList() {
    appointment = new ConsultationAppointment();
    appointment.setId(1L);
    appointment.setDoctor(doctor);
    appointment.setLocation(location);
    appointment.setDateFrom(dateTime.minusDays(1));
    appointment.setDateTo(dateTime.plusDays(1));
    appointment.setService(service);

    when(appointmentRepository.findByLocationIdAndDoctorId(location.getId(), doctor.getId()))
        .thenReturn(List.of(appointment));
    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(locationRepository.findByDoctorsSpecializationsServicesAndDoctorsId(service,
        doctor.getId()))
        .thenReturn(List.of(location));

    List<Location> result = locationService.getAvailableLocations(service.getId(),
        doctor.getId(), dateTime);

    assertTrue(result.isEmpty(),
        "Expected the result list to be empty due to overlapping appointment.");

    verify(hospitalServiceService).findById(service.getId());
    verify(locationRepository)
        .findByDoctorsSpecializationsServicesAndDoctorsId(service, doctor.getId());
    verify(appointmentRepository)
        .findByLocationIdAndDoctorId(location.getId(), doctor.getId());
  }

  @Test
  void getAvailableLocations_NonOverlappingAppointments_ReturnsLocationList() {
    appointment = new ConsultationAppointment();
    appointment.setId(1L);
    appointment.setDoctor(doctor);
    appointment.setLocation(location);
    appointment.setDateFrom(dateTime.minusDays(10));
    appointment.setDateTo(dateTime.minusDays(5));
    appointment.setService(service);

    when(appointmentRepository.findByLocationIdAndDoctorId(location.getId(), doctor.getId()))
        .thenReturn(List.of(appointment));
    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(locationRepository.findByDoctorsSpecializationsServicesAndDoctorsId(service,
        doctor.getId()))
        .thenReturn(List.of(location));

    List<Location> result = locationService
        .getAvailableLocations(service.getId(), doctor.getId(), dateTime);

    assertEquals(List.of(location), result,
        "Expected the result list to contain the available location.");

    verify(hospitalServiceService).findById(service.getId());
    verify(locationRepository)
        .findByDoctorsSpecializationsServicesAndDoctorsId(service, doctor.getId());
    verify(appointmentRepository)
        .findByLocationIdAndDoctorId(location.getId(), doctor.getId());
  }

  @Test
  void getAvailableLocations_WithoutDateTime_ReturnsLocationList() {
    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(locationRepository.findByDoctorsSpecializationsServicesAndDoctorsId(service,
        doctor.getId()))
        .thenReturn(List.of(location));

    List<Location> result =
        locationService.getAvailableLocations(service.getId(), doctor.getId(), null);

    assertEquals(List.of(location), result,
        "Expected the result list to contain the available location.");

    verify(hospitalServiceService).findById(service.getId());
    verify(locationRepository)
        .findByDoctorsSpecializationsServicesAndDoctorsId(service, doctor.getId());
  }

  @Test
  void findById_WrongId_ExceptionThrown() {
    Long locationId = 4L;
    when(locationRepository.findById(locationId)).thenThrow(
        new EntityNotFoundException(Location.class, locationId)
    );
    assertThrows(EntityNotFoundException.class, () ->
        locationService.findById(locationId));
  }

  @Test
  void findById_ValidId_ReturnsLocation() {
    Long locationId = 4L;
    when(locationRepository.findById(locationId)).thenReturn(Optional.of(new Location()));
    locationService.findById(locationId);
    verify(locationRepository).findById(locationId);
  }

  @Test
  void testGetAvailableLocationsByTestId_ValidId_ReturnLocations() {
    Location location1 = new Location();
    location1.setId(1L);
    location1.setHospitalName("Hospital A");

    Location location2 = new Location();
    location2.setId(2L);
    location2.setHospitalName("Hospital B");

    List<Location> mockLocations = Arrays.asList(location1, location2);
    Long testId = 1L;

    when(medicalTestService.findById(testId)).thenReturn(new MedicalTest());
    when(locationRepository.findAllByTestsIdOrderByHospitalNameAsc(testId))
        .thenReturn(mockLocations);

    List<Location> result = locationService.getAvailableLocationsByTestId(testId);

    assertEquals(2, result.size());
    assertEquals("Hospital A", result.get(0).getHospitalName());
    assertEquals("Hospital B", result.get(1).getHospitalName());

    verify(medicalTestService).findById(testId);
    verify(locationRepository).findAllByTestsIdOrderByHospitalNameAsc(testId);
  }
}
