package com.ventionteams.medfast.service.appointment;

import com.ventionteams.medfast.dto.request.RescheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.request.ScheduleMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.response.PdfResultResponse;
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
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.exception.medicaltestappointment.MissingPdfForMedicalTestException;
import com.ventionteams.medfast.exception.medicaltestappointment.PdfForMedicalTestAlreadyExistsException;
import com.ventionteams.medfast.exception.patient.PatientMismatchException;
import com.ventionteams.medfast.pdf.TestAppointmentPdfGenerator;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import com.ventionteams.medfast.service.LocationService;
import com.ventionteams.medfast.service.MedicalTestService;
import com.ventionteams.medfast.service.TimeSlotService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Medical test appointments service responsible operations related to medical appointments.
 */
@Service
@RequiredArgsConstructor
public class MedicalTestAppointmentService {

  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;
  private final MedicalTestService medicalTestService;
  private final TestAppointmentPdfGenerator testAppointmentPdfGenerator;
  private final LocationService locationService;
  private final TimeSlotService timeSlotService;
  private final EmailAppointmentService emailAppointmentService;

  /**
   * Schedules medical test appointment for given user.
   */
  @Transactional
  public MedicalTestAppointment scheduleMedicalTestAppointment(
      ScheduleMedicalTestAppointmentRequest request,
      User authenticatedUser) throws MessagingException {
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    MedicalTest test = medicalTestService.findById(request.getTestId());

    LocalDateTime dateTime = request.getDateTime().toLocalDateTime();
    timeSlotService.validateTimeAvailability(authenticatedPatient, dateTime,
        dateTime.plusMinutes(test.getDuration()));

    MedicalTestAppointment medicalTestAppointment = medicalTestAppointmentRepository.save(
        MedicalTestAppointment.builder()
            .pdf(null)
            .test(test)
            .patient(authenticatedPatient)
            .status(AppointmentStatus.SCHEDULED_CONFIRMED)
            .dateTime(dateTime)
            .location(locationService.findById(request.getLocationId()))
            .build());

    emailAppointmentService.sendBookMedicalTestAppointmentEmail(authenticatedUser,
        medicalTestAppointment.getLocation(), medicalTestAppointment.getDateTime());
    return medicalTestAppointment;
  }

  /**
   * Provides list of tests for given user.
   */
  public List<MedicalTestAppointment> getMedicalTests(User user,
      Optional<Integer> amount,
      AppointmentRequestType type) {
    int testAmount = amount.orElse(2);
    if (testAmount < 0) {
      throw new NegativeAppointmentsAmountException(
          user.getId(), testAmount);
    }
    return getFilteredTests(user, type, testAmount);
  }

  private List<MedicalTestAppointment> getFilteredTests(User authenticatedUser,
      AppointmentRequestType type,
      int amount) {
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    LocalDateTime now = LocalDateTime.now();
    return medicalTestAppointmentRepository.findAllByPatientOrderByDateTimeDesc(
            authenticatedPatient).stream().filter(appointment ->
            type == AppointmentRequestType.PAST
                ? appointment.getDateTime().isBefore(now)
                : appointment.getDateTime().isAfter(now)
        )
        .limit(amount > 0 ? amount : Long.MAX_VALUE)
        .toList();
  }

  /**
   * Generates test result PDF for given test. Accessible only for admin, also used by job
   * scheduler.
   */
  public void generateTestResultForAppointment(Long testAppointmentId) {

    MedicalTestAppointment testAppointment =
        findById(testAppointmentId);
    if (testAppointment.getPdf() == null) {
      testAppointmentPdfGenerator.generateAndSaveTestPdf(testAppointment);
    } else {
      throw new PdfForMedicalTestAlreadyExistsException(testAppointmentId);
    }
  }

  /**
   * Provides test result PDF for given test.
   */
  public PdfResultResponse getTestResult(User authenticatedUser, Long testAppointmentId) {
    MedicalTestAppointment medicalTestAppointment =
        findById(testAppointmentId);
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    if (!Objects.equals(medicalTestAppointment.getPatient().getId(), authenticatedPatient.getId())
        && authenticatedUser.getRole().equals(Role.PATIENT)) {
      throw new PatientMismatchException(authenticatedUser.getEmail(),
          medicalTestAppointment.getId());
    }
    if (medicalTestAppointment.getPdf() == null) {
      throw new MissingPdfForMedicalTestException(medicalTestAppointment.getId());
    }

    return PdfResultResponse.builder()
        .name(getPdfName(medicalTestAppointment))
        .data(medicalTestAppointment.getPdf())
        .build();
  }

  /**
   * Provides name of PDF result for given test.
   */
  public String getPdfName(MedicalTestAppointment test) {
    String patientName = test.getPatient().getName().concat("_")
        .concat(test.getPatient().getSurname());
    String dateOfTest = test.getDateTime().toString();
    return patientName + "_" + dateOfTest + ".pdf";
  }

  public List<MedicalTestAppointment> findTestAppointmentsByDate(
      LocalDateTime start, LocalDateTime end) {
    return medicalTestAppointmentRepository.findAllByDateTimeBetween(start, end);
  }

  public void changeStatus(MedicalTestAppointment appointment, AppointmentStatus status) {
    appointment.setStatus(status);
    medicalTestAppointmentRepository.save(appointment);
  }

  /**
   * Private method to get MedicalTestAppointment by ID.
   */
  private MedicalTestAppointment findById(Long testAppointmentId) {
    return medicalTestAppointmentRepository.findById(testAppointmentId)
        .orElseThrow(() -> new EntityNotFoundException(
            MedicalTestAppointment.class, testAppointmentId));
  }

  /**
   * Reschedules consultation appointment for the given patient.
   */
  public void rescheduleAppointment(RescheduleMedicalTestAppointmentRequest request,
      User authenticatedUser) throws MessagingException {
    final Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    final MedicalTestAppointment appointment = this.findById(request.getAppointmentId());
    final Location newLocation = locationService.findByIdAndMedicalTest(request.getLocationId(),
        appointment.getTest());
    final LocalDateTime newDateFromUtc = request.getDateFrom().toLocalDateTime();

    timeSlotService.validateTimeAvailability(authenticatedPatient, newDateFromUtc,
        newDateFromUtc.plusMinutes(appointment.getTest().getDuration()));
    if (!Objects.equals(appointment.getPatient().getId(), authenticatedPatient.getId())) {
      throw new PatientMismatchException(authenticatedUser.getEmail(), request.getAppointmentId());
    }

    appointment.setDateTime(newDateFromUtc);
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    if (request.getLocationId() != null) {
      appointment.setLocation(newLocation);
    }
    medicalTestAppointmentRepository.save(appointment);
    emailAppointmentService.sendRescheduleMedicalTestAppointmentEmail(authenticatedUser,
        newLocation, newDateFromUtc);
  }

  /**
   * Finds medical test appointment and cancels it if provided patient owns it.
   */
  @Transactional
  public void cancelTestAppointment(Long appointmentId,
      User authenticatedUser) throws MessagingException {
    MedicalTestAppointment appointment = this.findById(appointmentId);

    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();

    if (!Objects.equals(appointment.getPatient().getId(), authenticatedPatient.getId())) {
      throw new PatientMismatchException(authenticatedUser.getEmail(), appointmentId);
    }

    validateStatusForAppointmentCancelling(appointment);
    changeStatus(appointment, AppointmentStatus.CANCELLED_PATIENT);
    emailAppointmentService.sendCancelMedicalTestAppointmentEmail(authenticatedUser,
        appointment.getLocation(), appointment.getDateTime());
  }

  private void validateStatusForAppointmentCancelling(MedicalTestAppointment appointment) {
    if (appointment.getStatus() == AppointmentStatus.CANCELLED_PATIENT
        || appointment.getStatus() == AppointmentStatus.CANCELLED_CLINIC) {
      throw new AppointmentAlreadyCancelledException(
          MedicalTestAppointment.class, appointment.getId());
    } else if (appointment.getStatus() == AppointmentStatus.COMPLETED
        || appointment.getStatus() == AppointmentStatus.MISSED) {
      throw new CompletedOrMissedAppointmentException(
          MedicalTestAppointment.class, appointment.getId());
    }
  }
}
