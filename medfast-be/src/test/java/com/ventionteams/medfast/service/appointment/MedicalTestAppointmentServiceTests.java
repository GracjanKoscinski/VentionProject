package com.ventionteams.medfast.service.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.request.RescheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.appointment.AppointmentAlreadyCancelledException;
import com.ventionteams.medfast.exception.appointment.CompletedOrMissedAppointmentException;
import com.ventionteams.medfast.exception.appointment.TimeOccupiedException;
import com.ventionteams.medfast.exception.patient.PatientMismatchException;
import com.ventionteams.medfast.pdf.TestAppointmentPdfGenerator;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import com.ventionteams.medfast.service.LocationService;
import com.ventionteams.medfast.service.MedicalTestService;
import com.ventionteams.medfast.service.TimeSlotService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the medical test appointment service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class MedicalTestAppointmentServiceTests {

  @InjectMocks
  private MedicalTestAppointmentService medicalTestAppointmentService;
  @Mock
  private TimeSlotService timeSlotService;
  @Mock
  private EmailAppointmentService emailService;
  @Mock
  private MedicalTestService medicalTestService;
  @Mock
  private LocationService locationService;
  @Mock
  private TestAppointmentPdfGenerator testAppointmentPdfGenerator;
  @Mock
  private MedicalTestAppointmentRepository medicalTestAppointmentRepository;
  private static RescheduleMedicalTestAppointmentRequest rescheduleAppointmentRequest;
  private static MedicalTestAppointment appointment;
  private static MedicalTest test;
  private static Location location;
  private static User patientUser;
  private static Location newLocation;
  private static Patient patient;

  @BeforeAll
  static void setUp() {
    patient = Patient.builder().id(2L).name("John").surname("Doe").build();
    patientUser = User.builder()
        .person(patient).build();
    test = MedicalTest.builder().duration(15L).build();
    appointment = MedicalTestAppointment.builder()
        .id(33L)
        .test(test)
        .patient(patient)
        .dateTime(LocalDateTime.now().plusHours(2))
        .status(AppointmentStatus.SCHEDULED)
        .build();
    location = Location.builder()
        .id(37L)
        .build();
    newLocation = Location.builder()
        .id(3L)
        .build();
    rescheduleAppointmentRequest = RescheduleMedicalTestAppointmentRequest.builder()
        .appointmentId(appointment.getId())
        .dateFrom(ZonedDateTime.now().plusDays(1))
        .locationId(newLocation.getId())
        .build();
  }

  @Test
  public void scheduleMedicalTest_TimeOccupied_ExceptionThrown() {
    ScheduleMedicalTestAppointmentRequest request = ScheduleMedicalTestAppointmentRequest.builder()
        .testId(test.getId())
        .locationId(1L)
        .dateTime(ZonedDateTime.now().minusDays(1))
        .build();

    when(medicalTestService.findById(test.getId())).thenReturn(test);
    doThrow(new TimeOccupiedException(request.getDateTime().toLocalDateTime()))
        .when(timeSlotService).validateTimeAvailability(any(Patient.class),
            any(LocalDateTime.class), any(LocalDateTime.class));

    assertThrows(TimeOccupiedException.class, () ->
        medicalTestAppointmentService.scheduleMedicalTestAppointment(request, patientUser));

    verify(medicalTestAppointmentRepository, never()).save(any(MedicalTestAppointment.class));
  }

  @Test
  public void scheduleMedicalTest_ValidRequest_ReturnsMedicalTest() throws MessagingException {
    when(medicalTestService.findById(test.getId())).thenReturn(test);
    when(locationService.findById(location.getId())).thenReturn(location);
    when(medicalTestAppointmentRepository.save(any(MedicalTestAppointment.class)))
        .thenReturn(new MedicalTestAppointment());

    ScheduleMedicalTestAppointmentRequest request = ScheduleMedicalTestAppointmentRequest.builder()
        .testId(test.getId())
        .locationId(location.getId())
        .dateTime(ZonedDateTime.now().plusDays(1))
        .build();
    medicalTestAppointmentService.scheduleMedicalTestAppointment(request, patientUser);

    verify(medicalTestAppointmentRepository).save(any(MedicalTestAppointment.class));
  }

  @Test
  public void getMedicalTest_PastType_ReturnsList() {
    User admin = new User();
    admin.setRole(Role.ADMIN);
    Optional<Integer> amount = Optional.of(0);
    AppointmentRequestType type = AppointmentRequestType.PAST;
    List<MedicalTestAppointment> medicalTestAppointments = List.of(
        MedicalTestAppointment.builder()
            .dateTime(LocalDateTime.now().minusDays(1))
            .build()
    );
    when(medicalTestAppointmentRepository.findAllByPatientOrderByDateTimeDesc(
        (Patient) patientUser.getPerson())).thenReturn(medicalTestAppointments);

    List<MedicalTestAppointment> medicalTestAppointmentResponse
        = medicalTestAppointmentService.getMedicalTests(patientUser, amount, type);

    Assertions.assertThat(medicalTestAppointmentResponse).isEqualTo(medicalTestAppointments);
  }

  @Test
  void generateTestResultForAppointment_AppointmentNotFound_ExceptionThrown() {
    when(medicalTestAppointmentRepository.findById(1L)).thenReturn(Optional.empty());
    User admin = new User();
    admin.setRole(Role.ADMIN);
    assertThrows(EntityNotFoundException.class, () ->
        medicalTestAppointmentService.generateTestResultForAppointment(1L));

  }

  @Test
  void generateTestResultForAppointment_GeneratesTestResult() {

    User user = new User();
    Patient patient = new Patient();
    patient.setName("John");
    patient.setSurname("Doe");
    user.setPerson(patient);

    MedicalTestAppointment appointment = new MedicalTestAppointment();
    appointment.setPatient(patient);
    appointment.setId(1L);
    appointment.setLocation(new Location());
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    appointment.setDateTime(LocalDateTime.now());

    User admin = new User();
    admin.setRole(Role.ADMIN);

    when(medicalTestAppointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

    medicalTestAppointmentService.generateTestResultForAppointment(1L);
    verify(testAppointmentPdfGenerator).generateAndSaveTestPdf(any(MedicalTestAppointment.class));
  }

  @Test
  void getPdfName_ValidInput_ReturnsPdfName() {
    String pdfName = medicalTestAppointmentService.getPdfName(appointment);
    assertEquals("John_Doe_" + appointment.getDateTime() + ".pdf", pdfName);
  }

  @Test
  public void changeStatus_ValidInput() {
    MedicalTestAppointment appointment = new MedicalTestAppointment();
    AppointmentStatus newStatus = AppointmentStatus.COMPLETED;

    medicalTestAppointmentService.changeStatus(appointment, newStatus);

    verify(medicalTestAppointmentRepository).save(appointment);
    assertEquals(newStatus, appointment.getStatus());
  }

  @Test
  void rescheduleAppointment_ValidRequest_Success() throws MessagingException {
    when(medicalTestAppointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(locationService.findByIdAndMedicalTest(rescheduleAppointmentRequest.getLocationId(), test))
        .thenReturn(newLocation);

    medicalTestAppointmentService.rescheduleAppointment(rescheduleAppointmentRequest, patientUser);

    verify(medicalTestAppointmentRepository).save(appointment);
    verify(emailService).sendRescheduleMedicalTestAppointmentEmail(patientUser,
        newLocation, rescheduleAppointmentRequest.getDateFrom().toLocalDateTime());
  }

  @Test
  void rescheduleAppointment_PatientMismatch_ExceptionThrown() {
    User wrongPatientUser = User.builder().person(new Patient()).build();

    when(medicalTestAppointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(PatientMismatchException.class, () ->
        medicalTestAppointmentService.rescheduleAppointment(rescheduleAppointmentRequest,
            wrongPatientUser)
    );

    verify(medicalTestAppointmentRepository, never()).save(appointment);
  }

  @Test
  void rescheduleAppointment_TimeSlotOccupied_ExceptionThrown() {
    when(medicalTestAppointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(locationService.findByIdAndMedicalTest(rescheduleAppointmentRequest.getLocationId(), test))
        .thenReturn(newLocation);
    doThrow(new TimeOccupiedException(rescheduleAppointmentRequest.getDateFrom().toLocalDateTime()))
        .when(timeSlotService).validateTimeAvailability(any(Patient.class),
            any(LocalDateTime.class), any(LocalDateTime.class));

    assertThrows(TimeOccupiedException.class, () ->
        medicalTestAppointmentService.rescheduleAppointment(rescheduleAppointmentRequest,
            patientUser)
    );

    verify(medicalTestAppointmentRepository, never()).save(appointment);
  }

  @Test
  void cancelTestAppointment_ValidRequest_SuccessfulCancellation() throws MessagingException {
    MedicalTestAppointment appointment = MedicalTestAppointment.builder()
        .id(7L).patient(patient).location(new Location()).dateTime(LocalDateTime.now()).build();

    when(medicalTestAppointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));
    doNothing().when(emailService).sendCancelMedicalTestAppointmentEmail(
        eq(patientUser), any(Location.class), any(LocalDateTime.class));

    medicalTestAppointmentService.cancelTestAppointment(appointment.getId(), patientUser);

    verify(medicalTestAppointmentRepository).save(appointment);
    verify(emailService).sendCancelMedicalTestAppointmentEmail(
        any(User.class), any(Location.class), any(LocalDateTime.class));
    assertEquals(AppointmentStatus.CANCELLED_PATIENT, appointment.getStatus());
  }

  @Test
  void cancelTestAppointment_PatientMismatch_ExceptionThrown() {
    Patient otherPatient = Patient.builder().id(11L).build();
    MedicalTestAppointment appointment = MedicalTestAppointment.builder()
        .id(7L).patient(otherPatient).build();

    when(medicalTestAppointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(PatientMismatchException.class, () ->
        medicalTestAppointmentService.cancelTestAppointment(appointment.getId(), patientUser));
  }

  @Test
  void cancelTestAppointment_AlreadyCancelled_ExceptionThrown() {
    MedicalTestAppointment appointment = MedicalTestAppointment.builder()
        .id(7L).patient(patient).status(AppointmentStatus.CANCELLED_PATIENT).build();

    when(medicalTestAppointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(AppointmentAlreadyCancelledException.class, () ->
        medicalTestAppointmentService.cancelTestAppointment(appointment.getId(), patientUser)
    );
  }

  @Test
  void cancelTestAppointment_CompletedOrMissed_ExceptionThrown() {
    MedicalTestAppointment appointment = MedicalTestAppointment.builder()
        .id(7L).patient(patient).status(AppointmentStatus.COMPLETED).build();

    when(medicalTestAppointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(CompletedOrMissedAppointmentException.class, () ->
        medicalTestAppointmentService.cancelTestAppointment(appointment.getId(), patientUser)
    );
  }
}
