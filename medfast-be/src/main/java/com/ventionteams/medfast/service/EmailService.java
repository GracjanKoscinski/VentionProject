package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SupportConfig;
import com.ventionteams.medfast.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email service responsible for sending emails to users.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService {

  private static final String LOGO_PATH = "templates/logos/logo.png";
  private static final String WATERMARK_PATH = "templates/logos/watermark.png";

  private static final String VERIFICATION_EMAIL_SUBJECT = "Medfast: Complete Your Registration";
  private static final String RESET_PASSWORD_EMAIL_SUBJECT = "Medfast: Reset Your Password";

  private static final String TEMPORARY_PASSWORD_EMAIL_TEMPLATE = "temporary_password";
  private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "password_reset";

  private final JavaMailSender emailSender;
  private final TemplateEngine templateEngine;
  private final UrlService urlService;
  private final SpringConfig springConfig;
  private final SupportConfig supportConfig;

  /**
   * Send an email with temporary password.
   */
  public void sendTemporaryPasswordEmail(User user, String pwd, String code)
      throws MessagingException {
    Context context = new Context();
    context.setVariable("password", pwd);
    context.setVariable("setPermanentPasswordLink",
        urlService.generateSetPermanentPasswordUrl(user.getEmail(), code));
    context.setVariable("supportMailbox", springConfig.mail().username());
    context.setVariable("supportPhone", supportConfig.phoneNumber());
    String content = templateEngine.process(TEMPORARY_PASSWORD_EMAIL_TEMPLATE, context);
    sendEmailWithRetries(springConfig.mail().maxRetries(), VERIFICATION_EMAIL_SUBJECT,
        user.getEmail(), content);
  }

  /**
   * Send a reset password email to the user with a one time password.
   */
  public void sendResetPasswordEmail(User user, String token)
      throws MessagingException {
    Context context = new Context();
    context.setVariable("token", token);
    context.setVariable("supportMailbox", springConfig.mail().username());
    String content = templateEngine.process(RESET_PASSWORD_EMAIL_TEMPLATE, context);
    sendEmailWithRetries(springConfig.mail().maxRetries(), RESET_PASSWORD_EMAIL_SUBJECT,
        user.getEmail(), content);
  }

  /**
   * Tries to send email multiple times if error occurs.
   */
  public void sendEmailWithRetries(
      int maxRetries,
      String template,
      String email,
      String content) throws MessagingException {

    int retries = 0;
    boolean mailSuccessfullySent = false;
    while (!mailSuccessfullySent && retries < maxRetries) {
      try {
        sendMimeMessage(template, email, content);
        mailSuccessfullySent = true;
      } catch (MailAuthenticationException e) {
        log.error("Error occurred during attempt to send a mail: " + e.getMessage());
        retries++;
        if (retries == maxRetries) {
          throw e;
        }
      }
    }
  }

  /**
   * Sends an email with the specified subject, recipient and content.
   */
  public void sendMimeMessage(String subject, String recipient, String content)
      throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

    helper.setSubject(subject);
    helper.setFrom(springConfig.mail().username());
    helper.setTo(recipient);
    helper.setText(content, true);
    helper.addInline("logo", new ClassPathResource(LOGO_PATH));
    helper.addInline("watermark", new ClassPathResource(WATERMARK_PATH));

    emailSender.send(message);
    log.info("Email sent to {}, with subject and data: {}, {}", recipient, subject, content);
  }
}
