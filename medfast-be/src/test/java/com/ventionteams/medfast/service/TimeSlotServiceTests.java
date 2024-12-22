package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.WorkdayConfig;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

/**
 * Checks time slot service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class TimeSlotServiceTests {

  @Nested
  class TimeSlotTests {

    @Test
    void timeSlot_Equals_SameAttributes_ShouldReturnTrue() {
      LocalDateTime startTime = LocalDateTime.of(2024, 8, 22, 10, 0);
      LocalDateTime endTime = LocalDateTime.of(2024, 8, 22, 11, 0);

      TimeSlotService.TimeSlot slot1 = new TimeSlotService.TimeSlot(startTime, endTime);
      TimeSlotService.TimeSlot slot2 = new TimeSlotService.TimeSlot(startTime, endTime);

      Assertions.assertEquals(slot1, slot2);
    }

    @Test
    void timeSlot_Equals_DifferentStartTime_ShouldReturnFalse() {
      LocalDateTime startTime1 = LocalDateTime.of(2024, 8, 22, 10, 0);
      LocalDateTime endTime1 = LocalDateTime.of(2024, 8, 22, 11, 0);

      LocalDateTime startTime2 = LocalDateTime.of(2024, 8, 22, 9, 0);
      LocalDateTime endTime2 = LocalDateTime.of(2024, 8, 22, 11, 0);

      TimeSlotService.TimeSlot slot1 = new TimeSlotService.TimeSlot(startTime1, endTime1);
      TimeSlotService.TimeSlot slot2 = new TimeSlotService.TimeSlot(startTime2, endTime2);

      Assertions.assertNotEquals(slot1, slot2);
    }

    @Test
    void timeSlot_Equals_DifferentEndTime_ShouldReturnFalse() {
      LocalDateTime startTime1 = LocalDateTime.of(2024, 8, 22, 10, 0);
      LocalDateTime endTime1 = LocalDateTime.of(2024, 8, 22, 11, 0);

      LocalDateTime startTime2 = LocalDateTime.of(2024, 8, 22, 10, 0);
      LocalDateTime endTime2 = LocalDateTime.of(2024, 8, 22, 12, 0);

      TimeSlotService.TimeSlot slot1 = new TimeSlotService.TimeSlot(startTime1, endTime1);
      TimeSlotService.TimeSlot slot2 = new TimeSlotService.TimeSlot(startTime2, endTime2);

      Assertions.assertNotEquals(slot1, slot2);
    }

    @Test
    void timeSlot_HashCode_SameAttributes_ShouldReturnSameHashCode() {
      LocalDateTime startTime = LocalDateTime.of(2024, 8, 22, 10, 0);
      LocalDateTime endTime = LocalDateTime.of(2024, 8, 22, 11, 0);

      TimeSlotService.TimeSlot slot1 = new TimeSlotService.TimeSlot(startTime, endTime);
      TimeSlotService.TimeSlot slot2 = new TimeSlotService.TimeSlot(startTime, endTime);

      Assertions.assertEquals(slot1.hashCode(), slot2.hashCode());
    }
  }

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  @Mock
  private WorkdayConfig workdayConfig;

  @Mock
  private MedicalTestService medicalTestService;

  @InjectMocks
  private TimeSlotService timeSlotService;

  private final Long testId = 1L;
  private final Long locationId = 1L;
  private Integer month = 10;
  private Integer year = 2024;
  private List<MedicalTestAppointment> appointments;
  private MedicalTest test;
  private Location location;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    Class<?> testClass = testInfo.getTestClass().orElseThrow(NoSuchElementException::new);
    if (!testClass.isAnnotationPresent(Nested.class)) {
      test = new MedicalTest();
      test.setId(testId);

      location = new Location();
      location.setId(locationId);

      MedicalTestAppointment appointment1 = new MedicalTestAppointment();
      appointment1.setDateTime(LocalDateTime.of(2024, 10, 10, 10, 0));
      appointment1.setTest(test);
      appointment1.setLocation(location);

      MedicalTestAppointment appointment2 = new MedicalTestAppointment();
      appointment2.setDateTime(LocalDateTime.of(2024, 10, 15, 14, 0));
      appointment2.setTest(test);
      appointment2.setLocation(location);

      appointments = List.of(appointment1, appointment2);
    }
  }

  @Test
  void generateTimeSlotsForDay_ValidServiceAndDateTime_ReturnsExpectedSlots() {
    HospitalService service = new HospitalService();
    service.setDuration(90L);
    LocalDateTime localDateTime =
        LocalDateTime.of(2024, 8, 22, 0, 0);

    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    List<TimeSlot> timeSlots = timeSlotService.generateTimeSlotsForDay(
        service.getDuration(), localDateTime);

    Assertions.assertNotNull(timeSlots);
    Assertions.assertEquals(5, timeSlots.size());

    TimeSlotService.TimeSlot firstSlot = timeSlots.get(0);
    TimeSlotService.TimeSlot lastSlot = timeSlots.get(timeSlots.size() - 1);

    Assertions.assertEquals(LocalDateTime.of(2024, 8, 22, 9, 0),
        firstSlot.getStartTime());
    Assertions.assertEquals(LocalDateTime.of(2024, 8, 22, 10, 30),
        firstSlot.getEndTime());
    Assertions.assertEquals(LocalDateTime.of(2024, 8, 22, 15, 0),
        lastSlot.getStartTime());
    Assertions.assertEquals(LocalDateTime.of(2024, 8, 22, 16, 30),
        lastSlot.getEndTime());
  }

  @Test
  void getOccupiedTimeSlotsForMonth_StartsInsideAndEndsOutsideSlot_ReturnsExpectedOccupiedSlots() {
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDateFrom(
        LocalDateTime.of(2024, 8, 22, 12, 0));
    appointment.setDateTo(LocalDateTime.of(2024, 8, 22, 13, 0));
    Doctor doctor = new Doctor();
    HospitalService service = new HospitalService();
    service.setDuration(30L);

    when(appointmentRepository.findByPatientOrDoctorAndStatusNotInAndStartEndDate(
        eq(doctor), anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(appointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    LocalDateTime date = LocalDateTime.of(2024, 8, 22, 0, 0);
    List<TimeSlotService.TimeSlot> occupiedSlots = timeSlotService.getOccupiedTimeSlotsForMonth(
        doctor, service, date, null);

    Assertions.assertNotNull(occupiedSlots);
    Assertions.assertEquals(2, occupiedSlots.size());

    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 12, 0),
        LocalDateTime.of(2024, 8, 22, 12, 30))));
    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 12, 30),
        LocalDateTime.of(2024, 8, 22, 13, 0))));
  }

  @Test
  void getOccupiedTimeSlotsForMonth_StartsAndEndsOutsideSlot_ReturnsExpectedOccupiedSlots() {
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDateFrom(
        LocalDateTime.of(2024, 8, 22, 11, 45));
    appointment.setDateTo(LocalDateTime.of(2024, 8, 22, 12, 45));
    Doctor doctor = new Doctor();
    HospitalService service = new HospitalService();
    service.setDuration(30L);

    when(appointmentRepository.findByPatientOrDoctorAndStatusNotInAndStartEndDate(
        eq(doctor), anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(appointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    LocalDateTime date = LocalDateTime.of(2024, 8, 22, 0, 0);
    List<TimeSlotService.TimeSlot> occupiedSlots = timeSlotService.getOccupiedTimeSlotsForMonth(
        doctor, service, date, null);

    Assertions.assertNotNull(occupiedSlots);
    Assertions.assertEquals(3, occupiedSlots.size());

    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 11, 30),
        LocalDateTime.of(2024, 8, 22, 12, 0))));
    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 12, 0),
        LocalDateTime.of(2024, 8, 22, 12, 30))));
    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 12, 30),
        LocalDateTime.of(2024, 8, 22, 13, 0))));
  }

  @Test
  void getOccupiedTimeSlotsForMonth_StartsAndEndsInsideSlot_ReturnsExpectedOccupiedSlots() {
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDateFrom(
        LocalDateTime.of(2024, 8, 22, 12, 30));
    appointment.setDateTo(LocalDateTime.of(2024, 8, 22, 13, 0));
    Doctor doctor = new Doctor();
    HospitalService service = new HospitalService();
    service.setDuration(90L);

    when(appointmentRepository.findByPatientOrDoctorAndStatusNotInAndStartEndDate(
        eq(doctor), anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(appointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    LocalDateTime date = LocalDateTime.of(2024, 8, 22, 0, 0);
    List<TimeSlotService.TimeSlot> occupiedSlots = timeSlotService.getOccupiedTimeSlotsForMonth(
        doctor, service, date, null);

    Assertions.assertNotNull(occupiedSlots);
    Assertions.assertEquals(1, occupiedSlots.size());

    assertTrue(occupiedSlots.contains(new TimeSlotService.TimeSlot(
        LocalDateTime.of(2024, 8, 22, 12, 0),
        LocalDateTime.of(2024, 8, 22, 13, 30))));
  }

  @Test
  void getAvailableTimeSlotsForMedicalTests_NoOccupiedSlots_ReturnsAllTimeSlots() {
    when(medicalTestService.getTestDuration(testId)).thenReturn(45L);

    when(medicalTestAppointmentRepository
        .findAll(ArgumentMatchers.<Specification<MedicalTestAppointment>>any()))
        .thenReturn(appointments);
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    List<TimeSlot> availableSlots = timeSlotService
        .getAvailableTimeSlotsForMedicalTests(testId, locationId, month, year);

    Assertions.assertNotNull(availableSlots);
    Assertions.assertFalse(availableSlots.isEmpty());

    Assertions.assertEquals(LocalDateTime.of(2024, 10, 1, 9, 0), availableSlots
        .get(0).getStartTime());
    Assertions.assertEquals(LocalDateTime.of(2024, 10, 1, 9, 45), availableSlots
        .get(0).getEndTime());

    Assertions.assertEquals(LocalDateTime.of(2024, 10, 31, 15, 45), availableSlots
        .get(availableSlots.size() - 1).getStartTime());
    Assertions.assertEquals(LocalDateTime.of(2024, 10, 31, 16, 30), availableSlots
        .get(availableSlots.size() - 1).getEndTime());
  }

  @Test
  void getAvailableTimeSlotsForMedicalTests_WithOccupiedSlots_FiltersOutOccupiedSlots() {
    when(medicalTestService.getTestDuration(testId)).thenReturn(30L);

    when(medicalTestAppointmentRepository
        .findAll(ArgumentMatchers.<Specification<MedicalTestAppointment>>any()))
        .thenReturn(appointments);
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    List<TimeSlot> availableSlots = timeSlotService
        .getAvailableTimeSlotsForMedicalTests(testId, locationId, month, year);

    Assertions.assertNotNull(availableSlots);
    Assertions.assertFalse(availableSlots.isEmpty());

    Assertions.assertFalse(availableSlots.contains(new TimeSlot(
        LocalDateTime.of(2024, 10, 1, 9, 0),
        LocalDateTime.of(2024, 10, 1, 10, 0)
    )));

    Assertions.assertFalse(availableSlots.contains(new TimeSlot(
        LocalDateTime.of(2024, 8, 15, 14, 0),
        LocalDateTime.of(2024, 8, 15, 15, 0)
    )));
  }

  @Test
  void getAvailableTimeSlotsForMedicalTests_OccupiedSlotAtEndOfMonth_FiltersOutCorrectly() {
    when(medicalTestService.getTestDuration(testId)).thenReturn(30L);

    MedicalTestAppointment occupiedAppointment = new MedicalTestAppointment();
    occupiedAppointment.setDateTime(LocalDateTime
        .of(2024, 10, 31, 16, 0));

    when(medicalTestAppointmentRepository
        .findAll(ArgumentMatchers.<Specification<MedicalTestAppointment>>any()))
        .thenReturn(List.of(occupiedAppointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    List<TimeSlot> availableSlots = timeSlotService
        .getAvailableTimeSlotsForMedicalTests(testId, locationId, month, year);

    Assertions.assertNotNull(availableSlots);
    Assertions.assertFalse(availableSlots.isEmpty());

    Assertions.assertFalse(availableSlots.stream().anyMatch(slot ->
        slot.getStartTime().equals(LocalDateTime.of(2024, 10, 31, 16, 0))
            && slot.getEndTime().equals(LocalDateTime.of(2024, 10, 31, 16, 30))
    ));
  }

  @Test
  void getAvailableTimeSlotsForMedicalTests_AllSlotsOccupied_ReturnsEmptyList() {
    when(medicalTestService.getTestDuration(testId)).thenReturn(30L);

    LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 9, 0);
    LocalDateTime endOfMonth = LocalDateTime.of(year, month, 31, 17, 0);

    List<MedicalTestAppointment> occupiedAppointments = new ArrayList<>();
    for (LocalDateTime date = startOfMonth; date.isBefore(endOfMonth);
        date = date.plusMinutes(30)) {
      MedicalTestAppointment appointment = new MedicalTestAppointment();
      appointment.setDateTime(date);
      appointment.setTest(test);
      appointment.setLocation(location);
      occupiedAppointments.add(appointment);
    }

    when(medicalTestAppointmentRepository
        .findAll(ArgumentMatchers.<Specification<MedicalTestAppointment>>any()))
        .thenReturn(occupiedAppointments);
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    List<TimeSlot> availableSlots = timeSlotService
        .getAvailableTimeSlotsForMedicalTests(testId, locationId, month, year);

    Assertions.assertNotNull(availableSlots);
    Assertions.assertTrue(availableSlots.isEmpty());
  }

  @Test
  public void isDoctorAvailable_NoAppointments_ReturnsTrue() {
    Doctor doctor = new Doctor();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByDoctorAndStatusNotIn(doctor, excludedStatuses)).thenReturn(
        Collections.emptyList());

    boolean isAvailable = timeSlotService.isDoctorAvailable(doctor, from, to);

    assertTrue(isAvailable);
  }

  @Test
  public void isDoctorAvailable_AppointmentsDoNotOverlap_ReturnsTrue() {
    Doctor doctor = new Doctor();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .dateFrom(LocalDateTime.now().plusHours(2)).dateTo(LocalDateTime.now().plusHours(3))
        .build();
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByDoctorAndStatusNotIn(doctor, excludedStatuses)).thenReturn(
        Collections.singletonList(appointment));

    boolean isAvailable = timeSlotService.isDoctorAvailable(doctor, from, to);

    assertTrue(isAvailable);
  }

  @Test
  public void isDoctorAvailable_AppointmentsOverlap_ReturnsFalse() {
    Doctor doctor = new Doctor();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .dateFrom(LocalDateTime.now()).dateTo(LocalDateTime.now().plusHours(2)).build();
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByDoctorAndStatusNotIn(doctor, excludedStatuses)).thenReturn(
        Collections.singletonList(appointment));

    boolean isAvailable = timeSlotService.isDoctorAvailable(doctor, from, to);

    assertFalse(isAvailable);
  }

  @Test
  public void isPatientAvailable_NoAppointments_ReturnsTrue() {
    Patient patient = new Patient();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses)).thenReturn(
        Collections.emptyList());
    when(medicalTestAppointmentRepository.findByPatientAndStatusNotIn(patient,
        excludedStatuses)).thenReturn(
        Collections.emptyList());

    boolean isAvailable = timeSlotService.isPatientAvailable(patient, from, to);

    assertTrue(isAvailable);
  }

  @Test
  public void isPatientAvailable_PatientAppointmentsDoNotOverlap_ReturnsTrue() {
    Patient patient = new Patient();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .dateFrom(LocalDateTime.now().plusHours(2)).dateTo(LocalDateTime.now().plusHours(3))
        .build();
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses)).thenReturn(
        Collections.singletonList(appointment));
    when(medicalTestAppointmentRepository.findByPatientAndStatusNotIn(patient,
        excludedStatuses)).thenReturn(
        Collections.emptyList());

    boolean isAvailable = timeSlotService.isPatientAvailable(patient, from, to);

    assertTrue(isAvailable);
  }

  @Test
  public void isPatientAvailable_PatientAppointmentsOverlap_ReturnsFalse() {
    Patient patient = new Patient();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .dateFrom(LocalDateTime.now()).dateTo(LocalDateTime.now().plusHours(2)).build();
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses)).thenReturn(
        Collections.singletonList(appointment));
    when(medicalTestAppointmentRepository.findByPatientAndStatusNotIn(patient,
        excludedStatuses)).thenReturn(
        Collections.emptyList());

    boolean isAvailable = timeSlotService.isPatientAvailable(patient, from, to);

    assertFalse(isAvailable);
  }

  @Test
  public void isPatientAvailable_MedicalTestAppointmentsOverlap_ReturnsFalse() {
    Patient patient = new Patient();
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = from.plusHours(1);
    MedicalTestAppointment testAppointment = MedicalTestAppointment.builder()
        .dateTime(LocalDateTime.now())
        .test(MedicalTest.builder().duration(15L).build())
        .build();
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    when(appointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses)).thenReturn(
        Collections.emptyList());
    when(medicalTestAppointmentRepository.findByPatientAndStatusNotIn(patient,
        excludedStatuses)).thenReturn(
        Collections.singletonList(testAppointment));

    boolean isAvailable = timeSlotService.isPatientAvailable(patient, from, to);

    assertFalse(isAvailable);
  }

  @Test
  void getOccupiedTimeSlotsForMonth_WithLocationId_ShouldReturnOccupiedSlots() {
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDateFrom(
        LocalDateTime.of(2024, 8, 22, 11, 45));
    appointment.setDateTo(LocalDateTime.of(2024, 8, 22, 12, 45));
    Doctor doctor = new Doctor();
    HospitalService service = new HospitalService();
    service.setDuration(30L);

    when(appointmentRepository.findByPatientOrDoctorAndLocationAndStatusNotInAndStartEndDate(
        eq(doctor), eq(locationId),
        anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(appointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    LocalDateTime date = LocalDateTime.of(2024, 8, 22, 0, 0);
    List<TimeSlotService.TimeSlot> occupiedSlots =
        timeSlotService.getOccupiedTimeSlotsForMonth(doctor, service, date, locationId);

    assertEquals(3, occupiedSlots.size());
    assertEquals(LocalDateTime.of(2024, 8, 22, 11, 30), occupiedSlots.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2024, 8, 22, 12, 00), occupiedSlots.get(0).getEndTime());
  }

  @Test
  void getOccupiedTimeSlotsForMonth_WithoutLocationId_ShouldReturnOccupiedSlots() {
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDateFrom(
        LocalDateTime.of(2024, 8, 22, 11, 45));
    appointment.setDateTo(LocalDateTime.of(2024, 8, 22, 12, 45));
    Doctor doctor = new Doctor();
    HospitalService service = new HospitalService();
    service.setDuration(30L);

    when(appointmentRepository.findByPatientOrDoctorAndStatusNotInAndStartEndDate(
        eq(doctor), anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(appointment));
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    LocalDateTime date = LocalDateTime.of(2024, 8, 22, 0, 0);
    List<TimeSlotService.TimeSlot> occupiedSlots =
        timeSlotService.getOccupiedTimeSlotsForMonth(doctor, service, date, null);

    assertEquals(3, occupiedSlots.size());
    assertEquals(LocalDateTime.of(2024, 8, 22, 11, 30), occupiedSlots.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2024, 8, 22, 12, 00), occupiedSlots.get(0).getEndTime());
  }

  @Test
  public void testGetAvailableTimeSlotsForPatientAndDoctor_Success() {
    Patient patient = new Patient();
    User authenticatedUser = new User();
    authenticatedUser.setPerson(patient);
    HospitalService service = new HospitalService();
    service.setDuration(30L);
    month = 10;
    year = 2024;

    when(appointmentRepository
        .findByPatientOrDoctorAndLocationAndStatusNotInAndStartEndDate(
            any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    when(medicalTestAppointmentRepository
        .findAll(ArgumentMatchers.<Specification<MedicalTestAppointment>>any()))
        .thenReturn(Collections.emptyList());
    when(workdayConfig.startTime()).thenReturn(LocalTime.of(9, 0));
    when(workdayConfig.endTime()).thenReturn(LocalTime.of(17, 0));

    Doctor doctor = new Doctor();
    List<TimeSlot> availableTimeSlots = timeSlotService.getAvailableTimeSlotsForPatientAndDoctor(
        authenticatedUser, doctor, service, locationId, month, year);

    assertNotNull(availableTimeSlots);
    assertFalse(availableTimeSlots.isEmpty());
  }
}