package com.ventionteams.medfast.service.appointment;

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
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.ConsultationAppointmentType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.appointment.AppointmentAlreadyCancelledException;
import com.ventionteams.medfast.exception.appointment.CompletedOrMissedAppointmentException;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
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
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
public class ConsultationAppointmentService {

  private final EmailAppointmentService emailAppointmentService;
  private final AppointmentRepository repository;
  private final LocationService locationService;
  private final HospitalServiceService hospitalServiceService;
  private final TimeSlotService timeSlotService;
  private final DoctorService doctorService;
  private final AppointmentsToResponseMapper appointmentsToResponseMapper;
  private final AppConfig appConfig;

  /**
   * Provides the appointments for the given person.
   */
  @Transactional
  public List<AppointmentResponse> getAppointments(Optional<Person> person,
      Optional<Integer> amount,
      AppointmentRequestType type) {

    int appointmentAmount = amount.orElse(0);
    if (appointmentAmount < 0) {
      throw new NegativeAppointmentsAmountException(
          person.get().getUser().getId(), appointmentAmount);
    }
    return getFilteredAppointments(person, type, appointmentAmount);
  }

  private List<AppointmentResponse> getFilteredAppointments(Optional<Person> person,
      AppointmentRequestType type, int amount) {

    LocalDateTime now = LocalDateTime.now();
    return person
        .map(p -> repository.findAllByPatientOrDoctorOrderByDateFromAsc(p).stream()
            .filter(appointment ->
                type == AppointmentRequestType.PAST
                    ? appointment.getDateFrom().isBefore(now)
                    : appointment.getDateFrom().isAfter(now)
            )
            .sorted(Comparator.comparing(ConsultationAppointment::getDateFrom))
            .limit(amount > 0 ? amount : Long.MAX_VALUE)
            .toList())
        .map(appointmentsToResponseMapper)
        .orElseGet(ArrayList::new);
  }

  /**
   * Schedules a new consultation appointment for the given patient.
   */
  @Transactional
  public ConsultationAppointment scheduleConsultationAppointment(
      ScheduleConsultationAppointmentRequest request,
      User authenticatedUser) throws MessagingException {
    final Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    final Doctor doctor = doctorService.findById(request.getDoctorId());
    final Location location = getDoctorLocationIfAvailable(doctor, request.getLocationId());
    final HospitalService service = hospitalServiceService.findById(request.getServiceId());
    final LocalDateTime dateFromUtc = request.getDateFrom().toLocalDateTime();

    this.validateDoctorServiceAndTimeAvailability(doctor, service, authenticatedPatient,
        dateFromUtc);

    ConsultationAppointment scheduledAppointment = repository.save(
        ConsultationAppointment.builder()
            .service(service)
            .location(location)
            .patient(authenticatedPatient)
            .doctor(doctor)
            .dateFrom(dateFromUtc)
            .dateTo(dateFromUtc.plusMinutes(service.getDuration()))
            .status(AppointmentStatus.SCHEDULED_CONFIRMED)
            .type(location == null
                ? ConsultationAppointmentType.ONLINE
                : ConsultationAppointmentType.ONSITE)
            .build());
    emailAppointmentService.sendBookConsultationAppointmentEmail(
        authenticatedUser, doctor, location, dateFromUtc);
    return scheduledAppointment;
  }

  public void changeStatus(ConsultationAppointment appointment,
      AppointmentStatus status) {
    appointment.setStatus(status);
    repository.save(appointment);
  }

  /**
   * Provides a list of consultation appointments for the given user.
   */
  public List<ConsultationAppointment> getConsultationAppointments(
      User authenticatedUser,
      Optional<ElementSelection> selection) {
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    ElementSelection elementSelection = selection.orElse(ElementSelection.REMAINING);
    List<ConsultationAppointment> appointments;
    if (elementSelection == ElementSelection.FIRST) {
      appointments = repository.findByStatusOrderByDateFromDesc(
          authenticatedPatient.getId(),
          AppointmentStatus.COMPLETED.name(), 0, appConfig.elementCountLimit());
    } else {
      appointments = repository.findByStatusOrderByDateFromDesc(
          authenticatedPatient.getId(),
          AppointmentStatus.COMPLETED.name(), appConfig.elementCountLimit(), 1000);
    }
    return appointments;
  }

  /**
   * Finds appointment by appointment id or throws an error if appointment was not found.
   *
   * @return ConsultationAppointment
   */
  public ConsultationAppointment findById(Long appointmentId) {
    return repository.findById(appointmentId)
        .orElseThrow(
            () -> new EntityNotFoundException(
                ConsultationAppointment.class, appointmentId));
  }

  /**
   * Finds consultation appointment and cancels it if provided patient owns it.
   */
  @Transactional
  public void cancelConsultationAppointment(Long appointmentId,
      User authenticatedUser) throws MessagingException {
    ConsultationAppointment appointment = this.findById(appointmentId);

    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    if (!Objects.equals(appointment.getPatient().getId(), authenticatedPatient.getId())) {
      throw new PatientMismatchException(authenticatedUser.getEmail(), appointmentId);
    }

    validateStatusForAppointmentCancelling(appointment);
    changeStatus(appointment, AppointmentStatus.CANCELLED_PATIENT);
    emailAppointmentService.sendCancelConsultationAppointmentEmail(authenticatedUser,
        appointment.getDoctor(), appointment.getLocation(), appointment.getDateFrom());
  }

  /**
   * Returns consultation appointment with specified id if provided user owns it.
   */
  public ConsultationAppointment getAppointment(Long appointmentId, User authenticatedUser) {
    Optional<ConsultationAppointment> appointment = repository.findById(appointmentId);
    if (appointment.isEmpty()) {
      throw new EntityNotFoundException(
          ConsultationAppointment.class, appointmentId);
    }
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    if (!Objects.equals(appointment.get().getPatient().getId(), authenticatedPatient.getId())) {
      throw new PatientMismatchException(authenticatedUser.getEmail(), appointmentId);
    }
    return appointment.get();
  }

  /**
   * Reschedules consultation appointment for the given patient.
   */
  public void rescheduleAppointment(RescheduleConsultationAppointmentRequest request,
      User authenticatedUser) throws MessagingException {
    final Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    final ConsultationAppointment appointment = this.findById(request.getAppointmentId());
    if (!Objects.equals(appointment.getPatient().getId(), authenticatedPatient.getId())) {
      throw new PatientMismatchException(authenticatedUser.getEmail(), request.getAppointmentId());
    }
    final Doctor newDoctor = doctorService.findById(request.getDoctorId());
    final Location newLocation = this.getDoctorLocationIfAvailable(newDoctor,
        request.getLocationId());
    final LocalDateTime newDateFromUtc = request.getDateFrom().toLocalDateTime();

    this.validateDoctorServiceAndTimeAvailability(newDoctor, appointment.getService(),
        authenticatedPatient, newDateFromUtc);

    appointment.setDoctor(newDoctor);
    appointment.setDateFrom(newDateFromUtc);
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    if (request.getLocationId() != null) {
      appointment.setLocation(newLocation);
    }
    repository.save(appointment);
    emailAppointmentService.sendRescheduleConsultationAppointmentEmail(authenticatedUser, newDoctor,
        newLocation, newDateFromUtc);
  }

  private Location getDoctorLocationIfAvailable(Doctor doctor, Long locationId) {
    if (locationId == null) {
      return null;
    }
    Location location = locationService.findById(locationId);
    if (!doctorService.isWorkingInLocation(doctor, locationId)) {
      throw new DoctorLocationMismatchException(doctor.getId(), location.getId());
    }
    return location;
  }

  private void validateDoctorServiceAndTimeAvailability(Doctor doctor, HospitalService service,
      Patient patient, LocalDateTime dateFromUtc) {
    if (!doctorService.isProviding(doctor, service)) {
      throw new ServiceNotProvidedException(doctor.getId(), service.getId());
    }

    timeSlotService.validateTimeAvailability(doctor, patient, dateFromUtc,
        dateFromUtc.plusMinutes(service.getDuration()));
  }

  private void validateStatusForAppointmentCancelling(ConsultationAppointment appointment) {
    if (appointment.getStatus() == AppointmentStatus.CANCELLED_PATIENT
        || appointment.getStatus() == AppointmentStatus.CANCELLED_CLINIC) {
      throw new AppointmentAlreadyCancelledException(
          ConsultationAppointment.class, appointment.getId());
    } else if (appointment.getStatus() == AppointmentStatus.COMPLETED
        || appointment.getStatus() == AppointmentStatus.MISSED) {
      throw new CompletedOrMissedAppointmentException(
          ConsultationAppointment.class, appointment.getId());
    }
  }
}
