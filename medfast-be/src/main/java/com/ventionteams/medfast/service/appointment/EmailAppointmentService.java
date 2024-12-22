package com.ventionteams.medfast.service.appointment;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SupportConfig;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.utils.TimeUtils;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email Appointment service responsible for sending emails to users.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EmailAppointmentService {

  private static final String BOOK_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your consultation appointment was booked";
  private static final String CANCEL_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your consultation appointment was canceled";
  private static final String RESCHEDULE_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your consultation appointment was rescheduled";
  private static final String BOOK_TEST_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your medical test appointment was booked";
  private static final String CANCEL_TEST_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your medical test appointment was canceled";
  private static final String RESCHEDULE_TEST_APPOINTMENT_EMAIL_SUBJECT =
      "Medfast: Your medical test appointment was rescheduled";
  private static final String BOOK_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/book_consultation_appointment";
  private static final String CANCEL_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/cancel_consultation_appointment";
  private static final String RESCHEDULE_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/reschedule_consultation_appointment";
  private static final String BOOK_TEST_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/book_test_appointment";
  private static final String CANCEL_TEST_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/cancel_test_appointment";
  private static final String RESCHEDULE_TEST_APPOINTMENT_EMAIL_TEMPLATE =
      "appointment/reschedule_test_appointment";

  private final TemplateEngine templateEngine;
  private final SpringConfig springConfig;
  private final SupportConfig supportConfig;
  private final EmailService emailService;

  /**
   * Sends a booking consultation appointment email.
   */
  public void sendBookConsultationAppointmentEmail(User user, Doctor doctor, Location location,
      LocalDateTime dateTime)
      throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, doctor);
    this.sendEmail(context, BOOK_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE,
        BOOK_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  /**
   * Sends a cancel consultation appointment email.
   */
  public void sendCancelConsultationAppointmentEmail(User user, Doctor doctor,
      Location location, LocalDateTime dateTime)
      throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, doctor);
    this.sendEmail(context, CANCEL_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE,
        CANCEL_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  /**
   * Sends a booking test appointment email.
   */
  public void sendBookMedicalTestAppointmentEmail(User user, Location location,
      LocalDateTime dateTime)
      throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, null);
    this.sendEmail(context, BOOK_TEST_APPOINTMENT_EMAIL_TEMPLATE,
        BOOK_TEST_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  /**
   * Sends a cancel test appointment email.
   */
  public void sendCancelMedicalTestAppointmentEmail(User user, Location location,
      LocalDateTime dateTime)
      throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, null);
    this.sendEmail(context, CANCEL_TEST_APPOINTMENT_EMAIL_TEMPLATE,
        CANCEL_TEST_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  /**
   * Sends a reschedule consultation appointment email.
   */
  public void sendRescheduleConsultationAppointmentEmail(User user, Doctor doctor,
      Location location, LocalDateTime dateTime)
      throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, doctor);
    this.sendEmail(context, RESCHEDULE_CONSULTATION_APPOINTMENT_EMAIL_TEMPLATE,
        RESCHEDULE_CONSULTATION_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  /**
   * Sends a reschedule medical test appointment email.
   */
  public void sendRescheduleMedicalTestAppointmentEmail(User user, Location location,
      LocalDateTime dateTime) throws MessagingException {
    Context context = this.createAppointmentEmailContext(location, dateTime, null);
    this.sendEmail(context, RESCHEDULE_TEST_APPOINTMENT_EMAIL_TEMPLATE,
        RESCHEDULE_TEST_APPOINTMENT_EMAIL_SUBJECT, user.getEmail());
  }

  private Context createAppointmentEmailContext(Location location, LocalDateTime dateTime,
      Doctor doctor) {
    Context context = new Context();
    if (doctor != null) {
      List<String> specializationNames = doctor.getSpecializations().stream()
          .map(Specialization::getSpecialization)
          .toList();
      String specializationsFormatted = String.join(" ", specializationNames);
      context.setVariable("doctor", String.format("%s Dr. %s %s",
          specializationsFormatted, doctor.getName(), doctor.getSurname()));
    }
    context.setVariable("location", location == null ? "" : "in " + location);
    context.setVariable("dateTime", TimeUtils.formatLocatDateTime(dateTime));
    context.setVariable("supportMailbox", springConfig.mail().username());
    context.setVariable("supportPhone", supportConfig.phoneNumber());

    return context;
  }

  private void sendEmail(Context context, String template, String subject, String userEmail)
      throws MessagingException {
    String content = templateEngine.process(template, context);
    emailService.sendEmailWithRetries(springConfig.mail().maxRetries(), subject, userEmail,
        content);
  }
}
