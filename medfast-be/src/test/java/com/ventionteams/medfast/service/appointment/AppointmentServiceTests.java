package com.ventionteams.medfast.service.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.dto.request.RescheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleConsultationAppointmentRequest;
import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.appointment.AppointmentAlreadyCancelledException;
import com.ventionteams.medfast.exception.appointment.CompletedOrMissedAppointmentException;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.exception.appointment.TimeOccupiedException;
import com.ventionteams.medfast.exception.location.DoctorLocationMismatchException;
import com.ventionteams.medfast.exception.patient.PatientMismatchException;
import com.ventionteams.medfast.exception.service.ServiceNotProvidedException;
import com.ventionteams.medfast.mapper.appointments.AppointmentsToResponseMapper;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.service.DoctorService;
import com.ventionteams.medfast.service.HospitalServiceService;
import com.ventionteams.medfast.service.LocationService;
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
 * Checks appointments service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTests {

  @Mock
  private AppConfig appConfig;
  @Mock
  private TimeSlotService timeSlotService;
  @Mock
  private HospitalServiceService hospitalServiceService;
  @Mock
  private DoctorService doctorService;
  @Mock
  private AppointmentRepository appointmentRepository;
  @Mock
  private LocationService locationService;
  @Mock
  private EmailAppointmentService emailService;
  @Mock
  private AppointmentsToResponseMapper appointmentsToResponseMapper;

  @InjectMocks
  private ConsultationAppointmentService appointmentService;

  private static ConsultationAppointment appointment;
  private static HospitalService hospitalService;
  private static Doctor doctor;
  private static Location location;
  private static Doctor newDoctor;
  private static Location newLocation;
  private static User patientUser;
  private static Patient patient;
  private static RescheduleConsultationAppointmentRequest rescheduleAppointmentRequest;
  private static ScheduleConsultationAppointmentRequest createOnlineAppointmentRequest;
  private static ScheduleConsultationAppointmentRequest createOnSiteAppointmentRequest;

  /**
   * Prepares entities to further tests.
   */
  @BeforeAll
  public static void setUp() {
    hospitalService = HospitalService.builder()
        .id(2L)
        .duration(60L)
        .build();
    location = Location.builder()
        .id(3L)
        .build();
    doctor = Doctor.builder()
        .id(5L)
        .location(location)
        .build();
    newLocation = Location.builder()
        .id(3L)
        .build();
    newDoctor = Doctor.builder()
        .id(55L)
        .location(newLocation)
        .build();
    patient = Patient.builder().id(2L).build();
    patientUser = User.builder()
        .person(patient).build();
    appointment = ConsultationAppointment.builder().id(77L).patient(patient)
        .service(hospitalService).build();
    createOnlineAppointmentRequest =
        ScheduleConsultationAppointmentRequest.builder()
            .serviceId(hospitalService.getId())
            .doctorId(doctor.getId())
            .dateFrom(ZonedDateTime.now().plusDays(1))
            .build();
    createOnSiteAppointmentRequest =
        ScheduleConsultationAppointmentRequest.builder()
            .serviceId(hospitalService.getId())
            .doctorId(doctor.getId())
            .dateFrom(ZonedDateTime.now().plusDays(1))
            .locationId(location.getId())
            .build();
    rescheduleAppointmentRequest =
        RescheduleConsultationAppointmentRequest.builder()
            .appointmentId(appointment.getId())
            .doctorId(newDoctor.getId())
            .locationId(newLocation.getId())
            .dateFrom(ZonedDateTime.now().plusDays(1))
            .build();
  }

  @Test
  void getAppointments_EmptyPerson_ReturnsEmptyArrayList() {
    Optional<Person> person = Optional.empty();
    Optional<Integer> amount = Optional.empty();
    AppointmentRequestType type = AppointmentRequestType.UPCOMING;

    List<AppointmentResponse> appointments = appointmentService
        .getAppointments(person, amount, type);

    Assertions.assertThat(appointments).isEmpty();
  }

  @Test
  void getAppointments_NegativeAmount_ExceptionThrown() {
    Optional<Person> person = Optional.of(
        Person.builder()
            .user(
                User.builder().id(1L).build()
            ).build());
    Optional<Integer> amount = Optional.of(-5);
    AppointmentRequestType type = AppointmentRequestType.UPCOMING;

    Assertions.assertThatThrownBy(() -> appointmentService.getAppointments(person, amount, type))
        .isInstanceOf(NegativeAppointmentsAmountException.class);
  }

  @Test
  void getAppointments_UpcomingType_ReturnsArrayList() {
    Optional<Person> person = Optional.of(
        Person.builder()
            .user(
                User.builder().id(1L).build()
            ).build());
    Optional<Integer> amount = Optional.of(5);
    AppointmentRequestType type = AppointmentRequestType.UPCOMING;
    List<ConsultationAppointment> consultationAppointments = List.of(
        ConsultationAppointment.builder()
            .dateFrom(LocalDateTime.now().plusDays(1))
            .build()
    );

    when(appointmentRepository.findAllByPatientOrDoctorOrderByDateFromAsc(person.get()))
        .thenReturn(consultationAppointments);

    List<AppointmentResponse> appointmentRespons = appointmentService
        .getAppointments(person, amount, type);

    Assertions.assertThat(appointmentRespons).isEqualTo(
        appointmentsToResponseMapper.apply(consultationAppointments)
    );
  }

  @Test
  void getAppointments_PastType_ReturnsArrayList() {
    Optional<Person> person = Optional.ofNullable(Patient.builder()
        .id(1L)
        .build());
    Optional<Integer> amount = Optional.of(5);
    AppointmentRequestType type = AppointmentRequestType.PAST;
    List<ConsultationAppointment> consultationAppointments = List.of(
        ConsultationAppointment.builder()
            .dateFrom(LocalDateTime.now().minusDays(1))
            .build()
    );

    when(appointmentRepository.findAllByPatientOrDoctorOrderByDateFromAsc(person.get()))
        .thenReturn(consultationAppointments);

    List<AppointmentResponse> appointmentResponses = appointmentService
        .getAppointments(person, amount, type);

    Assertions.assertThat(appointmentResponses).isEqualTo(
        appointmentsToResponseMapper.apply(consultationAppointments)
    );
  }

  @Test
  void getAppointments_ZeroAmount_ReturnsAllAppointments() {
    Optional<Person> person = Optional.ofNullable(Patient.builder()
        .id(1L)
        .build());
    Optional<Integer> amount = Optional.of(0);
    AppointmentRequestType type = AppointmentRequestType.PAST;
    List<ConsultationAppointment> consultationAppointments = List.of(
        ConsultationAppointment.builder()
            .dateFrom(LocalDateTime.now().minusDays(1))
            .build(),
        ConsultationAppointment.builder()
            .dateFrom(LocalDateTime.now().minusDays(2))
            .build()
    );

    when(appointmentRepository.findAllByPatientOrDoctorOrderByDateFromAsc(person.get()))
        .thenReturn(consultationAppointments);

    List<AppointmentResponse> appointmentResponses = appointmentService
        .getAppointments(person, amount, type);

    Assertions.assertThat(appointmentResponses).isEqualTo(
        appointmentsToResponseMapper.apply(consultationAppointments)
    );
  }

  @Test
  void scheduleOnlineAppointment_GoodRequest_ReturnsAppointment() throws MessagingException {

    when(doctorService.findById(anyLong())).thenReturn(doctor);
    when(hospitalServiceService.findById(anyLong())).thenReturn(hospitalService);
    when(doctorService.isProviding(doctor, hospitalService)).thenReturn(true);
    when(appointmentRepository.save(any(ConsultationAppointment.class)))
        .thenReturn(new ConsultationAppointment());

    appointmentService.scheduleConsultationAppointment(createOnlineAppointmentRequest, patientUser);

    verify(doctorService).findById(anyLong());
    verify(hospitalServiceService).findById(anyLong());
    verify(doctorService).isProviding(any(), any());
    verify(timeSlotService).validateTimeAvailability(any(), any(), any(), any());
    verify(appointmentRepository).save(any(ConsultationAppointment.class));
    verify(emailService).sendBookConsultationAppointmentEmail(any(), any(), any(), any());
  }

  @Test
  void scheduleOnlineAppointment_DoctorNotFound_ExceptionThrown() {

    when(doctorService.findById(anyLong())).thenThrow(EntityNotFoundException.class);

    Assertions.assertThatThrownBy(() -> appointmentService
            .scheduleConsultationAppointment(createOnlineAppointmentRequest, patientUser))
        .isInstanceOf(EntityNotFoundException.class);

    verify(doctorService).findById(anyLong());
  }

  @Test
  void scheduleOnlineAppointment_ServiceNotProvided_ExceptionThrown() {

    when(doctorService.findById(anyLong())).thenReturn(doctor);
    when(hospitalServiceService.findById(anyLong())).thenReturn(hospitalService);
    when(doctorService.isProviding(doctor, hospitalService))
        .thenThrow(ServiceNotProvidedException.class);

    Assertions.assertThatThrownBy(() -> appointmentService
            .scheduleConsultationAppointment(createOnlineAppointmentRequest, patientUser))
        .isInstanceOf(ServiceNotProvidedException.class);

    verify(doctorService).findById(anyLong());
    verify(hospitalServiceService).findById(anyLong());
    verify(doctorService).isProviding(any(), any());
    verify(timeSlotService, never()).isPatientAvailable(any(), any(), any());
  }

  @Test
  void scheduleOnlineAppointment_TimeOccupiedForDoctorOrDoctor_ExceptionThrown() {

    when(doctorService.findById(anyLong())).thenReturn(doctor);
    when(hospitalServiceService.findById(anyLong())).thenReturn(hospitalService);
    when(doctorService.isProviding(doctor, hospitalService)).thenReturn(true);
    doThrow(new TimeOccupiedException(
        createOnlineAppointmentRequest.getDateFrom().toLocalDateTime()))
        .when(timeSlotService)
        .validateTimeAvailability(any(Doctor.class), any(Patient.class),
            any(LocalDateTime.class), any(LocalDateTime.class));

    Assertions.assertThatThrownBy(() -> appointmentService
            .scheduleConsultationAppointment(createOnlineAppointmentRequest, patientUser))
        .isInstanceOf(TimeOccupiedException.class);

    verify(doctorService).findById(anyLong());
    verify(hospitalServiceService).findById(anyLong());
    verify(doctorService).isProviding(any(), any());
    verify(timeSlotService).validateTimeAvailability(any(), any(), any(), any());
    verify(appointmentRepository, never()).save(any());
  }

  @Test
  void scheduleOnSiteAppointment_GoodRequest_ReturnsAppointment() throws MessagingException {

    when(doctorService.findById(anyLong())).thenReturn(doctor);
    when(locationService.findById(anyLong())).thenReturn(location);
    when(hospitalServiceService.findById(anyLong())).thenReturn(hospitalService);
    when(doctorService.isProviding(doctor, hospitalService)).thenReturn(true);
    when(doctorService.isWorkingInLocation(doctor, location.getId())).thenReturn(true);
    when(appointmentRepository.save(any(ConsultationAppointment.class)))
        .thenReturn(new ConsultationAppointment());

    appointmentService.scheduleConsultationAppointment(createOnSiteAppointmentRequest, patientUser);

    verify(locationService).findById(anyLong());
    verify(doctorService).findById(anyLong());
    verify(hospitalServiceService).findById(anyLong());
    verify(doctorService).isProviding(any(), any());
    verify(timeSlotService).validateTimeAvailability(any(), any(), any(), any());
    verify(appointmentRepository).save(any(ConsultationAppointment.class));
    verify(emailService).sendBookConsultationAppointmentEmail(any(), any(), any(), any());
  }

  @Test
  void scheduleOnSiteAppointment_DoctorLocationMismatch__ExceptionThrown() {

    when(doctorService.findById(anyLong())).thenReturn(doctor);
    when(locationService.findById(anyLong())).thenReturn(
        Location.builder().doctors(List.of(new Doctor())).build()
    );

    Assertions.assertThatThrownBy(() -> appointmentService
            .scheduleConsultationAppointment(createOnSiteAppointmentRequest, patientUser))
        .isInstanceOf(DoctorLocationMismatchException.class);
  }

  @Test
  public void getConsultationAppointments_WithoutElementSelection_ReturnsList() {
    Doctor doctor = Doctor.builder()
        .id(123L).name("Pawel").surname("Wisznia")
        .specializations(List.of(Specialization.builder().specialization("Cardiology").build()))
        .build();
    User.builder().person(doctor).build();
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDoctor(doctor);

    List<ConsultationAppointment> appointments = List.of();
    when(appointmentRepository.findByStatusOrderByDateFromDesc(
        patientUser.getPerson().getId(),
        AppointmentStatus.COMPLETED.name(),
        3,
        1000))
        .thenReturn(appointments);
    when(appConfig.elementCountLimit()).thenReturn(3);

    appointmentService.getConsultationAppointments(patientUser, Optional.empty());

    verify(appointmentRepository).findByStatusOrderByDateFromDesc(
        patient.getId(),
        AppointmentStatus.COMPLETED.name(),
        3,
        1000
    );
  }

  @Test
  public void getConsultationAppointments_WithElementSelection_ReturnsList() {
    Doctor doctor = Doctor.builder()
        .id(123L).name("Pawel").surname("Wisznia")
        .specializations(List.of(Specialization.builder().specialization("Cardiology").build()))
        .build();
    User.builder().person(doctor).build();
    ConsultationAppointment appointment = new ConsultationAppointment();
    appointment.setDoctor(doctor);

    List<ConsultationAppointment> appointments = List.of(appointment);
    when(appointmentRepository.findByStatusOrderByDateFromDesc(
        patientUser.getPerson().getId(),
        AppointmentStatus.COMPLETED.name(),
        0,
        3))
        .thenReturn(appointments);
    when(appConfig.elementCountLimit()).thenReturn(3);

    appointmentService.getConsultationAppointments(
        patientUser, Optional.of(ElementSelection.FIRST));

    verify(appointmentRepository).findByStatusOrderByDateFromDesc(
        patient.getId(),
        AppointmentStatus.COMPLETED.name(),
        0,
        3
    );
  }

  @Test
  void cancelConsultationAppointment_ValidRequest_SuccessfulCancellation()
      throws MessagingException {
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .id(7L).patient(patient).doctor(doctor).location(new Location())
        .dateFrom(LocalDateTime.now()).build();

    when(appointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));
    doNothing().when(emailService).sendCancelConsultationAppointmentEmail(
        eq(patientUser), eq(doctor), any(Location.class), any(LocalDateTime.class));

    appointmentService.cancelConsultationAppointment(appointment.getId(), patientUser);

    verify(appointmentRepository).save(appointment);
    verify(emailService).sendCancelConsultationAppointmentEmail(
        any(User.class), any(Doctor.class), any(Location.class), any(LocalDateTime.class)
    );
    assertEquals(AppointmentStatus.CANCELLED_PATIENT, appointment.getStatus());
  }

  @Test
  void cancelConsultationAppointment_PatientMismatch_ExceptionThrown() {
    Patient otherPatient = Patient.builder().id(11L).build();
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .id(7L).patient(otherPatient).build();

    when(appointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(PatientMismatchException.class, () ->
        appointmentService.cancelConsultationAppointment(appointment.getId(), patientUser)
    );
  }

  @Test
  void cancelConsultationAppointment_AlreadyCancelled_ExceptionThrown() {
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .id(7L).patient(patient).status(AppointmentStatus.CANCELLED_PATIENT).build();

    when(appointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(AppointmentAlreadyCancelledException.class, () ->
        appointmentService.cancelConsultationAppointment(appointment.getId(), patientUser)
    );
  }

  @Test
  void cancelConsultationAppointment_CompletedOrMissed_ExceptionThrown() {
    ConsultationAppointment appointment = ConsultationAppointment.builder()
        .id(7L).patient(patient).status(AppointmentStatus.COMPLETED).build();

    when(appointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(CompletedOrMissedAppointmentException.class, () ->
        appointmentService.cancelConsultationAppointment(appointment.getId(), patientUser)
    );
  }

  @Test
  void getAppointment_AppointmentExistsAndMatchesPatient_ReturnsAppointment() {
    Long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

    ConsultationAppointment result = appointmentService.getAppointment(appointmentId,
        patientUser);

    assertEquals(appointment, result);
    verify(appointmentRepository).findById(appointmentId);
  }

  @Test
  void getAppointment_AppointmentNotFound_ExceptionThrown() {
    Long appointmentId = 1L;
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> appointmentService.getAppointment(appointmentId, patientUser)
    );
    verify(appointmentRepository).findById(appointmentId);
  }

  @Test
  void getAppointment_PatientMismatch_ExceptionThrown() {
    Long appointmentId = 1L;
    Patient differentPatient = mock(Patient.class);
    ConsultationAppointment invalidAppointment = mock(ConsultationAppointment.class);
    when(differentPatient.getId()).thenReturn(5L);
    when(invalidAppointment.getPatient()).thenReturn(differentPatient);
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(invalidAppointment));

    assertThrows(
        PatientMismatchException.class,
        () -> appointmentService.getAppointment(appointmentId, patientUser)
    );
    verify(appointmentRepository).findById(appointmentId);
  }

  @Test
  void rescheduleAppointment_ValidRequest_Success() throws MessagingException {
    when(appointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(doctorService.findById(rescheduleAppointmentRequest.getDoctorId())).thenReturn(newDoctor);
    when(locationService.findById(rescheduleAppointmentRequest.getLocationId())).thenReturn(
        newLocation);
    when(doctorService.isWorkingInLocation(newDoctor,
        rescheduleAppointmentRequest.getLocationId())).thenReturn(true);
    when(doctorService.isProviding(newDoctor,
        hospitalService)).thenReturn(true);

    appointmentService.rescheduleAppointment(rescheduleAppointmentRequest, patientUser);

    verify(appointmentRepository).save(appointment);
    verify(emailService).sendRescheduleConsultationAppointmentEmail(patientUser, newDoctor,
        newLocation, rescheduleAppointmentRequest.getDateFrom().toLocalDateTime());
    verify(timeSlotService).validateTimeAvailability(any(), any(), any(), any());
  }

  @Test
  void rescheduleAppointment_PatientMismatch_ExceptionThrown() {
    User wrongPatientUser = User.builder().person(new Patient()).build();

    when(appointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));

    assertThrows(PatientMismatchException.class, () ->
        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest, wrongPatientUser)
    );

    verify(appointmentRepository, never()).save(appointment);
  }

  @Test
  void rescheduleAppointment_DoctorNotProvidingService_ExceptionThrown() {
    when(appointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(doctorService.findById(rescheduleAppointmentRequest.getDoctorId())).thenReturn(newDoctor);
    when(locationService.findById(rescheduleAppointmentRequest.getLocationId())).thenReturn(
        newLocation);
    when(doctorService.isWorkingInLocation(newDoctor,
        rescheduleAppointmentRequest.getLocationId())).thenReturn(true);
    when(doctorService.isProviding(newDoctor, appointment.getService())).thenReturn(false);

    assertThrows(ServiceNotProvidedException.class, () ->
        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest, patientUser)
    );

    verify(appointmentRepository, never()).save(appointment);
  }

  @Test
  void rescheduleAppointment_TimeSlotOccupied_ExceptionThrown() {
    when(appointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(doctorService.findById(rescheduleAppointmentRequest.getDoctorId())).thenReturn(newDoctor);
    when(locationService.findById(rescheduleAppointmentRequest.getLocationId())).thenReturn(
        newLocation);
    when(doctorService.isWorkingInLocation(newDoctor,
        rescheduleAppointmentRequest.getLocationId())).thenReturn(true);
    when(doctorService.isProviding(newDoctor,
        hospitalService)).thenReturn(true);
    doThrow(new TimeOccupiedException(rescheduleAppointmentRequest.getDateFrom().toLocalDateTime()))
        .when(timeSlotService)
        .validateTimeAvailability(any(Doctor.class), any(Patient.class),
            any(LocalDateTime.class), any(LocalDateTime.class));

    assertThrows(TimeOccupiedException.class, () ->
        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest, patientUser)
    );

    verify(appointmentRepository, never()).save(appointment);
  }

  @Test
  void rescheduleAppointment_DoctorNotWorkingInLocation_ExceptionThrown() {
    when(appointmentRepository.findById(rescheduleAppointmentRequest.getAppointmentId()))
        .thenReturn(Optional.of(appointment));
    when(doctorService.findById(rescheduleAppointmentRequest.getDoctorId())).thenReturn(newDoctor);
    when(locationService.findById(rescheduleAppointmentRequest.getLocationId())).thenReturn(
        newLocation);
    when(doctorService.isWorkingInLocation(newDoctor,
        rescheduleAppointmentRequest.getLocationId())).thenReturn(false);

    assertThrows(DoctorLocationMismatchException.class, () ->
        appointmentService.rescheduleAppointment(rescheduleAppointmentRequest, patientUser)
    );

    verify(appointmentRepository, never()).save(appointment);
  }
}
