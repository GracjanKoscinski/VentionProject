package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryWithAvailableSlotsResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks doctors service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class DoctorServiceTests {

  @Mock
  private DoctorRepository doctorRepository;

  @Mock
  private HospitalServiceService hospitalServiceService;

  @Mock
  private TimeSlotService timeSlotService;

  @InjectMocks
  private DoctorService doctorService;

  private static Doctor doctor;
  private static HospitalService service;

  @BeforeAll
  static void setUp() {
    doctor = Doctor.builder().id(3L).build();
    service = HospitalService.builder().id(1L).build();
    Specialization specialization = new Specialization();
    specialization.setServices(List.of(service));
    doctor.setSpecializations(List.of(specialization));
  }

  @Test
  void findById_ExistingId_ReturnsDoctor() {
    Long doctorId = 1L;
    when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

    Doctor foundDoctor = doctorService.findById(doctorId);

    assertEquals(doctor, foundDoctor);
    verify(doctorRepository).findById(doctorId);
  }

  @Test
  void findById_WrongId_ExceptionThrown() {
    when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> doctorService.findById(1L));
  }

  @Test
  void findDoctorsByService_NoDoctorsForService_ReturnsEmptyList() {
    Long serviceId = 1L;
    List<DoctorSummaryWithAvailableSlotsResponse> expectedList = new ArrayList<>();

    when(doctorRepository.findBySpecializationsServicesId(serviceId)).thenReturn(new ArrayList<>());

    List<Doctor> actualList = doctorService
        .findDoctorsByServiceAndFullName(serviceId, Optional.empty());

    Assertions.assertEquals(actualList, expectedList);
  }

  @Test
  void getOccupiedTimeSlotsForPatientAndDoctor_ValidRequest_ReturnsList() {
    User authenticatedUser = new User();
    List<TimeSlotResponse> response = List.of();

    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
    when(timeSlotService.getOccupiedTimeSlotsForPatientAndDoctor(
        eq(authenticatedUser), eq(doctor), eq(service), anyInt(), anyInt()))
        .thenReturn(response);

    Integer month = 11;
    Integer year = 2024;
    doctorService.getOccupiedTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor.getId(), service.getId(), month, year);

    verify(hospitalServiceService).findById(service.getId());
    verify(doctorRepository).findById(doctor.getId());
    verify(timeSlotService).getOccupiedTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor, service, month, year
    );
  }

  @Test
  void getAvailableTimeSlotsForPatientAndDoctor_ValidRequest_ReturnsAvailableTimeSlots() {
    User authenticatedUser = new User();
    Long locationId = 1L;
    Integer month = 11;
    Integer year = 2024;

    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));

    List<TimeSlot> expectedTimeSlots = List.of(
        new TimeSlot(LocalDateTime.of(year, month, 1, 9, 0),
            LocalDateTime.of(year, month, 1, 9, 30)),
        new TimeSlot(LocalDateTime.of(year, month, 1, 10, 0),
            LocalDateTime.of(year, month, 1, 10, 30))
    );
    when(timeSlotService.getAvailableTimeSlotsForPatientAndDoctor(
        eq(authenticatedUser), eq(doctor), eq(service), eq(locationId), eq(month), eq(year)))
        .thenReturn(expectedTimeSlots);

    List<TimeSlot> actualTimeSlots = doctorService.getAvailableTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor.getId(), service.getId(), locationId, month, year);

    assertEquals(expectedTimeSlots, actualTimeSlots);
    verify(hospitalServiceService).findById(service.getId());
    verify(doctorRepository).findById(doctor.getId());
    verify(timeSlotService).getAvailableTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor, service, locationId, month, year);
  }

  @Test
  void getAvailableTimeSlotsForPatientAndDoctor_InvalidDoctorId_ThrowsException() {
    User authenticatedUser = new User();
    Long locationId = 1L;
    Integer month = 11;
    Integer year = 2024;

    when(hospitalServiceService.findById(service.getId())).thenReturn(service);
    when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      doctorService.getAvailableTimeSlotsForPatientAndDoctor(
          authenticatedUser, doctor.getId(), service.getId(), locationId, month, year);
    });

    verify(hospitalServiceService).findById(service.getId());
    verify(doctorRepository).findById(doctor.getId());
    verify(timeSlotService, never())
        .getAvailableTimeSlotsForPatientAndDoctor(any(), any(), any(), any(), any(), any());
  }
}
