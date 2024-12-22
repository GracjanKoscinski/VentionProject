package com.ventionteams.medfast.service.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SpringConfig.Mail;
import com.ventionteams.medfast.config.properties.SupportConfig;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UrlService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Checks email verification service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTests {

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

  @Mock
  private EmailService emailService;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private EmailVerificationService emailVerificationService;

  @Test
  public void sendUserVerificationEmail_EmptyUser_ExceptionThrown() {
    Assertions.assertThrows(NullPointerException.class,
        () -> emailVerificationService.sendUserVerificationEmail(null));
  }

  @Test
  public void sendUserVerificationEmail_GoodSendEmail() throws MessagingException {
    Person person = mock(Person.class);
    String expectedContent = "<html>Verification content</html>";
    
    when(user.getPerson()).thenReturn(person);
    when(person.getName()).thenReturn("John Doe");
    when(user.getEmail()).thenReturn("user@example.com");

    when(urlService.generateVerificationUrl(user.getEmail()))
        .thenReturn("http://example.com/verify?token=12345");

    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");

    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn(expectedContent);
    when(springConfig.mail().maxRetries()).thenReturn(3);
    doNothing().when(emailService).sendEmailWithRetries(anyInt(), 
        anyString(), anyString(), anyString());

    emailVerificationService.sendUserVerificationEmail(user);

    verify(emailService).sendEmailWithRetries(3, 
        "Medfast: Complete Your Registration", 
        "user@example.com", 
        expectedContent);
  }
}