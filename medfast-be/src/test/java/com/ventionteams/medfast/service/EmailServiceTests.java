package com.ventionteams.medfast.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SpringConfig.Mail;
import com.ventionteams.medfast.config.properties.SupportConfig;
import com.ventionteams.medfast.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Checks email service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

  @Mock
  private JavaMailSender emailSender;

  @Mock
  private UrlService urlService;

  @Mock
  private SpringConfig springConfig;

  @Mock
  private SupportConfig supportConfig;

  @Mock
  private TemplateEngine templateEngine;

  @Mock
  private User user;

  @Mock
  private Mail mail;

  @InjectMocks
  private EmailService emailService;

  @Mock
  private MimeMessage mimeMessage;

  @Test
  public void sendResetPasswordEmail_CorrectInput_SendsEmail() throws MessagingException {
    String expectedContent = "<html>Reset password content</html>";

    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn(
        expectedContent);
    when(user.getEmail()).thenReturn("user@example.com");
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(springConfig.mail().maxRetries()).thenReturn(3);
    doNothing().when(emailSender).send(any(MimeMessage.class));

    emailService.sendResetPasswordEmail(user, "token");

    verify(emailSender).createMimeMessage();
    verify(emailSender).send(mimeMessage);
  }

  @Test
  public void sendTemporaryPasswordEmail_CorrectInput_SendsEmail() throws MessagingException {
    final String password = "abcd228";
    final String code = "1944";
    String expectedContent = "<html>Temporary password content</html>";

    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn(
        expectedContent);
    when(user.getEmail()).thenReturn("user@example.com");
    when(springConfig.mail().maxRetries()).thenReturn(3);
    when(supportConfig.phoneNumber()).thenReturn("222 777 999");
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(emailSender).send(any(MimeMessage.class));

    emailService.sendTemporaryPasswordEmail(user, password, code);

    verify(emailSender).createMimeMessage();
    verify(emailSender).send(mimeMessage);
  }

  @Test
  public void sendEmailWithRetries_ThreeAttemptsDueToException() throws MessagingException {
    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

    doThrow(new MailAuthenticationException("Test exception"))
        .when(emailSender).send(any(MimeMessage.class));

    Assertions.assertThrows(MailAuthenticationException.class,
        () -> emailService.sendEmailWithRetries(3, "template", "email", "content"));
    verify(emailSender, times(3)).send(mimeMessage);
  }
}
