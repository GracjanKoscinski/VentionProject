package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.WorkdayConfig;
import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.TimeType;
import com.ventionteams.medfast.exception.appointment.InvalidAppointmentTimeException;
import com.ventionteams.medfast.exception.appointment.TimeOccupiedException;
import com.ventionteams.medfast.exception.doctor.OutsideWorkingHoursException;
import com.ventionteams.medfast.mapper.TimeSlotsToResponseMapper;
import com.ventionteams.medfast.repository.AppointmentRepository;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import com.ventionteams.medfast.specification.MedicalTestAppointmentSpecifications;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service class for dealing with time slots.
 */
@Service
@RequiredArgsConstructor
public class TimeSlotService {

  private final AppointmentRepository appointmentRepository;
  private final TimeSlotsToResponseMapper timeSlotsToResponseMapper;
  private final WorkdayConfig workdayConfig;
  private final MedicalTestService medicalTestService;
  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  /**
   * Helper class to represent a time slot.
   */
  @AllArgsConstructor
  @Data
  @Builder
  public static class TimeSlot {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

  }

  /**
   * Provides a list of occupied time slots for the logged patient and provided doctor for the
   * month.
   */
  public List<TimeSlotResponse> getOccupiedTimeSlotsForPatientAndDoctor(
      User authenticatedUser, Doctor doctor, HospitalService service, Integer month, Integer year
  ) {
    Person authenticatedPerson = authenticatedUser.getPerson();

    LocalDateTime date = LocalDate.of(year, month, 1).atStartOfDay();

    List<TimeSlot> occupiedTimeSlots = Stream.concat(
            getOccupiedTimeSlotsForMonth(authenticatedPerson, service, date, null).stream(),
            getOccupiedTimeSlotsForMonth(doctor, service, date, null).stream()
        )
        .distinct()
        .toList();

    return timeSlotsToResponseMapper.apply(occupiedTimeSlots);
  }

  /**
   * Provides a list of occupied time slots for a particular physician for the month.
   */
  public List<TimeSlot> getOccupiedTimeSlotsForMonth(Person person, HospitalService service,
      LocalDateTime date, Long locationId) {
    List<ConsultationAppointment> consultationAppointments = getOccupiedConsultationAppointments(
        person, date, locationId);
    Long serviceDuration = service.getDuration();

    return consultationAppointments.stream()
        .flatMap(appointment -> {
          LocalDateTime dateFrom = appointment.getDateFrom();
          LocalDateTime dateTo = appointment.getDateTo();
          return generateTimeSlotsForDay(serviceDuration, dateFrom).stream()
              .filter(slot -> isOverlapping(slot, dateFrom, dateTo));
        })
        .distinct()
        .toList();
  }

  private List<ConsultationAppointment> getOccupiedConsultationAppointments(Person person,
      LocalDateTime date, Long locationId) {
    LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate()
        .atStartOfDay();
    LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate()
        .atTime(23, 59, 59, 999_999_999);
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    if (locationId != null) {
      return appointmentRepository
          .findByPatientOrDoctorAndLocationAndStatusNotInAndStartEndDate(
              person, locationId, excludedStatuses, startOfMonth, endOfMonth);
    } else {
      return appointmentRepository.findByPatientOrDoctorAndStatusNotInAndStartEndDate(
          person, excludedStatuses, startOfMonth, endOfMonth);
    }
  }

  /**
   * Retrieves a list of available time slots for medical tests based on the specified filters. This
   * method allows filtering by test ID, location, and start of month/end of month.
   */
  public List<TimeSlot> getAvailableTimeSlotsForMedicalTests(Long testId,
      Long locationId, Integer month, Integer year) {
    Long testDuration = medicalTestService.getTestDuration(testId);
    LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
    LocalDateTime endOfMonth = calculateEndOfMonth(year, month);
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);

    Specification<MedicalTestAppointment> specification = Specification.where(
        MedicalTestAppointmentSpecifications.hasTestId(testId)
    ).and(
        MedicalTestAppointmentSpecifications.hasLocationId(locationId)
    ).and(
        MedicalTestAppointmentSpecifications.hasDateTimeBetween(startOfMonth, endOfMonth)
    ).and(
        MedicalTestAppointmentSpecifications.hasStatusNotIn(excludedStatuses)
    );

    List<MedicalTestAppointment> occupiedAppointments = medicalTestAppointmentRepository
        .findAll(specification);
    List<TimeSlot> potentialTimeSlots = generateTimeSlotsForMonth(
        testDuration, startOfMonth, endOfMonth);

    return filterAvailableSlotsForMedicalTests(
        potentialTimeSlots, occupiedAppointments, testDuration);
  }

  /**
   * Calculates the end of the month, adjusting for leap years.
   */
  private LocalDateTime calculateEndOfMonth(int year, int month) {
    if (month == 2 && LocalDate.of(year, 1, 1).isLeapYear()) {
      return LocalDate.of(year, 2, 29).atTime(23, 59);
    } else {
      return LocalDate.of(year, month, 1)
          .with(TemporalAdjusters.lastDayOfMonth())
          .atTime(23, 59);
    }
  }

  /**
   * Generates a list of possible time slots for the entire month.
   */
  private List<TimeSlot> generateTimeSlotsForMonth(
      Long duration, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
    List<TimeSlot> timeSlots = new ArrayList<>();

    for (LocalDateTime day = startOfMonth; day.isBefore(endOfMonth); day = day.plusDays(1)) {
      timeSlots.addAll(generateTimeSlotsForDay(duration, day));
    }

    return timeSlots;
  }

  /**
   * Generates a list of possible time slots for one day, based on workday hours.
   */
  public List<TimeSlot> generateTimeSlotsForDay(
      Long duration, LocalDateTime dateTime) {
    List<TimeSlot> timeSlots = new ArrayList<>();
    for (LocalDateTime date = LocalDateTime.of(dateTime.toLocalDate(),
        workdayConfig.startTime());
        date.isBefore(LocalDateTime.of(dateTime.toLocalDate(),
            workdayConfig.endTime()));
        date = date.plusMinutes(duration)) {
      if (!date.plusMinutes(duration)
          .isAfter(LocalDateTime.of(dateTime.toLocalDate(),
              workdayConfig.endTime()))) {
        timeSlots.add(new TimeSlot(date, date.plusMinutes(duration)));
      }
    }

    return timeSlots;
  }

  /**
   * Filters out occupied time slots from the list of potential slots.
   */
  private List<TimeSlot> filterAvailableSlotsForMedicalTests(List<TimeSlot> potentialTimeSlots,
      List<MedicalTestAppointment> occupiedAppointments,
      Long testDuration) {
    return potentialTimeSlots.stream()
        .filter(slot -> occupiedAppointments.stream()
            .noneMatch(appointment -> isOverlapping(slot,
                appointment.getDateTime(),
                appointment.getDateTime().plusMinutes(testDuration)))).toList();
  }

  /**
   * Checks if a given time slot overlaps with a given period.
   */
  private boolean isOverlapping(TimeSlot slot, LocalDateTime dateFrom, LocalDateTime dateTo) {
    LocalDateTime slotStart = slot.getStartTime();
    LocalDateTime slotEnd = slot.getEndTime();

    boolean periodSlot = dateFrom.isBefore(slotStart) && dateTo.isAfter(slotEnd);
    boolean periodStartsWithinSlot = !dateFrom.isBefore(slotStart) && dateFrom.isBefore(slotEnd);
    boolean periodEndsWithinSlot = dateTo.isAfter(slotStart) && !dateTo.isAfter(slotEnd);

    return periodSlot || periodStartsWithinSlot || periodEndsWithinSlot;
  }

  /**
   * Validates that patient doesn't have an appointment at the specified time.
   */
  public void validateTimeAvailability(Patient patient,
      LocalDateTime from, LocalDateTime to) {
    this.validateAppointmentTime(from, to);
    if (!isPatientAvailable(patient, from, to)) {
      throw new TimeOccupiedException(from);
    }
  }

  /**
   * Validate that patient or doctor don't have an appointment at the specified time.
   */
  public void validateTimeAvailability(Doctor doctor, Patient patient,
      LocalDateTime from, LocalDateTime to) {
    this.validateTimeAvailability(patient, from, to);
    if (!this.isDoctorAvailable(doctor, from, to)) {
      throw new TimeOccupiedException(from);
    }
  }

  /**
   * Checks if doctor is available at the specified time.
   */
  public boolean isDoctorAvailable(Doctor doctor, LocalDateTime from, LocalDateTime to) {
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);
    List<ConsultationAppointment> doctorAppointments =
        appointmentRepository.findByDoctorAndStatusNotIn(doctor, excludedStatuses);
    return hasNoConflictingConsultationAppointments(doctorAppointments, from, to);
  }

  /**
   * Checks if patient is available at the specified time.
   */
  public boolean isPatientAvailable(Patient patient, LocalDateTime from, LocalDateTime to) {
    List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.CANCELLED_CLINIC,
        AppointmentStatus.CANCELLED_PATIENT);
    List<ConsultationAppointment> patientAppointments =
        appointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses);
    List<MedicalTestAppointment> patientTestAppointments =
        medicalTestAppointmentRepository.findByPatientAndStatusNotIn(patient, excludedStatuses);

    boolean hasNoConflictingAppointments = hasNoConflictingConsultationAppointments(
        patientAppointments, from, to);
    boolean hasNoConflictingTestAppointments = hasNoConflictingTestAppointments(
        patientTestAppointments, from, to);

    return hasNoConflictingAppointments && hasNoConflictingTestAppointments;
  }

  private boolean isOutsideWorkingHours(LocalDateTime from, LocalDateTime to) {
    return from.toLocalTime().isBefore(workdayConfig.startTime())
        || to.toLocalTime().isAfter(workdayConfig.endTime());
  }

  private boolean hasNoConflictingConsultationAppointments(
      List<ConsultationAppointment> appointments, LocalDateTime from, LocalDateTime to) {
    return appointments.stream()
        .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED_CLINIC
            && appointment.getStatus() != AppointmentStatus.CANCELLED_PATIENT)
        .noneMatch(appointment -> isOverlapping(
            new TimeSlot(appointment.getDateFrom(), appointment.getDateTo()), from, to));
  }

  private boolean hasNoConflictingTestAppointments(List<MedicalTestAppointment> appointments,
      LocalDateTime from, LocalDateTime to) {
    return appointments.stream()
        .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED_CLINIC
            && appointment.getStatus() != AppointmentStatus.CANCELLED_PATIENT)
        .noneMatch(appointment -> {
          LocalDateTime dateFrom = appointment.getDateTime();
          LocalDateTime dateTo = dateFrom.plusMinutes(appointment.getTest().getDuration());
          TimeSlot slot = new TimeSlot(dateFrom, dateTo);
          return isOverlapping(slot, from, to);
        });
  }

  private void validateAppointmentTime(LocalDateTime from, LocalDateTime to) {
    if (from.isBefore(LocalDateTime.now())) {
      throw new InvalidAppointmentTimeException(from, TimeType.PAST);
    }

    if (isOutsideWorkingHours(from, to)) {
      throw new OutsideWorkingHoursException("Appointment time is outside of working hours: "
          + workdayConfig.startTime() + " to " + workdayConfig.endTime());
    }
  }

  /**
   * Provides a list of available time slots for the logged patient and provided doctor for the
   * month.
   */
  public List<TimeSlot> getAvailableTimeSlotsForPatientAndDoctor(User authenticatedUser,
      Doctor doctor, HospitalService service, Long locationId, Integer month, Integer year) {

    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
    LocalDateTime endOfMonth = calculateEndOfMonth(year, month);
    Long serviceDuration = service.getDuration();
    List<TimeSlot> possibleTimeSlots =
        generateTimeSlotsForMonth(serviceDuration, startOfMonth, endOfMonth);
    List<TimeSlot> occupiedDoctorSlots =
        getOccupiedTimeSlotsForMonth(doctor, service, startOfMonth, locationId);
    List<TimeSlot> occupiedPatientConsultationSlots =
        getOccupiedTimeSlotsForMonth(authenticatedPatient, service, startOfMonth, locationId);
    List<TimeSlot> occupiedPatientTestSlots =
        getOccupiedMedicalTestSlotsForMonth(authenticatedPatient.getId(), startOfMonth, endOfMonth,
            locationId);
    List<TimeSlot> allOccupiedPatientSlots = Stream.concat(
        occupiedPatientConsultationSlots.stream(), occupiedPatientTestSlots.stream()
    ).distinct().toList();
    List<TimeSlot> allOccupiedSlots = Stream.concat(
        occupiedDoctorSlots.stream(), allOccupiedPatientSlots.stream()
    ).distinct().collect(Collectors.toList());
    return filterAvailableSlots(possibleTimeSlots, allOccupiedSlots);
  }

  /**
   * Retrieves a list of occupied time slots for medical tests during a given month for a specific
   * patient.
   */
  private List<TimeSlot> getOccupiedMedicalTestSlotsForMonth(
      Long patientId, LocalDateTime startOfMonth, LocalDateTime endOfMonth, Long locationId) {
    Specification<MedicalTestAppointment> specification = Specification.where(
        MedicalTestAppointmentSpecifications.hasPatientId(patientId)
    ).and(
        MedicalTestAppointmentSpecifications.hasDateTimeBetween(startOfMonth, endOfMonth)
    ).and(MedicalTestAppointmentSpecifications.hasLocationId(locationId));

    List<MedicalTestAppointment> occupiedAppointments =
        medicalTestAppointmentRepository.findAll(specification);

    return occupiedAppointments.stream()
        .map(appointment -> new TimeSlot(
            appointment.getDateTime(),
            appointment.getDateTime().plusMinutes(appointment.getTest().getDuration())
        ))
        .collect(Collectors.toList());
  }

  /**
   * Filters the available time slots by removing any that overlap with the occupied time slots.
   */
  private List<TimeSlot> filterAvailableSlots(
      List<TimeSlot> possibleTimeSlots, List<TimeSlot> occupiedSlots) {
    return possibleTimeSlots.stream()
        .filter(possibleSlot -> occupiedSlots.stream()
            .noneMatch(occupiedSlot -> isOverlapping(
                possibleSlot, occupiedSlot.getStartTime(), occupiedSlot.getEndTime())))
        .collect(Collectors.toList());
  }
}
