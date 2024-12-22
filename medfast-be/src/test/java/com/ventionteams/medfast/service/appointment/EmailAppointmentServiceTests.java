package com.ventionteams.medfast.service.appointment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SpringConfig.Mail;
import com.ventionteams.medfast.config.properties.SupportConfig;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.EmailService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Checks email appointment service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class EmailAppointmentServiceTests {


  @Mock
  private SpringConfig springConfig;

  @Mock
  private SupportConfig supportConfig;

  @Mock
  private TemplateEngine templateEngine;

  @Mock
  private Mail mail;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private EmailAppointmentService emailAppointmentService;
  private static Location location;
  private static Doctor doctor;
  private static String expectedContent;
  private static User user;
  private static LocalDateTime appointmentTime;

  @BeforeAll
  static void setUp() {
    appointmentTime = LocalDateTime
        .of(2024, 9, 9, 10, 0);
    expectedContent = "<html>Expected content</html>";
    user = User.builder().email("user@example.com").build();
    location = Location.builder()
        .hospitalName("Beth Moses").house("House 404").streetAddress("Hart Street").build();
    Specialization specialization1 = Specialization.builder().specialization("Therapist").build();
    Specialization specialization2 = Specialization.builder().specialization("Cardiologist")
        .build();
    doctor = Doctor.builder().name("Anton").surname("Doe")
        .specializations(List.of(specialization1, specialization2)).build();
  }

  @BeforeEach()
  void setUpMock() {
    when(mail.maxRetries()).thenReturn(3);
    when(springConfig.mail()).thenReturn(mail);
    when(supportConfig.phoneNumber()).thenReturn("333 888 000");
    when(mail.username()).thenReturn("support@example.com");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn(
        expectedContent);
  }

  @Test
  void sendBookConsultationAppointmentEmail_CorrectInput_SendsEmail() throws MessagingException {
    emailAppointmentService.sendBookConsultationAppointmentEmail(user, doctor, location,
        appointmentTime);
    verify(templateEngine).process(eq("appointment/book_consultation_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your consultation appointment was booked"),
        eq("user@example.com"), eq(expectedContent));
  }

  @Test
  void sendBookTestAppointmentEmail_CorrectInput_SendsEmail() throws MessagingException {
    emailAppointmentService.sendBookMedicalTestAppointmentEmail(user, location,
        appointmentTime);

    verify(templateEngine).process(eq("appointment/book_test_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your medical test appointment was booked"),
        eq("user@example.com"), eq(expectedContent));
  }

  @Test
  void sendRescheduleConsultationAppointmentEmail_CorrectInput_SendsEmail()
      throws MessagingException {
    emailAppointmentService.sendRescheduleConsultationAppointmentEmail(user, doctor,
        location, appointmentTime);

    verify(templateEngine).process(eq("appointment/reschedule_consultation_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your consultation appointment was rescheduled"),
        eq("user@example.com"), eq(expectedContent));
  }

  @Test
  void sendRescheduleMedicalTestAppointmentEmail_CorrectInput_SendsEmail()
      throws MessagingException {
    emailAppointmentService.sendRescheduleMedicalTestAppointmentEmail(user,
        location, appointmentTime);

    verify(templateEngine).process(eq("appointment/reschedule_test_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your medical test appointment was rescheduled"),
        eq("user@example.com"), eq(expectedContent));
  }

  @Test
  void sendCancelConsultationAppointmentEmail_CorrectInput_SendsEmail()
      throws MessagingException {
    emailAppointmentService.sendCancelConsultationAppointmentEmail(user, doctor,
        location, appointmentTime);

    verify(templateEngine).process(eq("appointment/cancel_consultation_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your consultation appointment was canceled"),
        eq("user@example.com"), eq(expectedContent));
  }

  @Test
  void sendCancelMedicalTestAppointmentEmail_CorrectInput_SendsEmail()
      throws MessagingException {
    emailAppointmentService.sendCancelMedicalTestAppointmentEmail(user,
        location, appointmentTime);

    verify(templateEngine).process(eq("appointment/cancel_test_appointment"),
        any(Context.class));
    verify(emailService).sendEmailWithRetries(eq(3),
        eq("Medfast: Your medical test appointment was canceled"),
        eq("user@example.com"), eq(expectedContent));
  }
}
