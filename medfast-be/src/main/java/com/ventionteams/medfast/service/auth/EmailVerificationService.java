package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UrlService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email verification service is focused on sending verification emails.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
  private static final String VERIFICATION_EMAIL_TEMPLATE = "verification";
  private static final String VERIFICATION_EMAIL_SUBJECT = "Medfast: Complete Your Registration";
  
  private final TemplateEngine templateEngine;
  private final UrlService verificationUrlService;
  private final EmailService emailService;
  private final SpringConfig springConfig;

  /**
   * Send a verification email to the user with a verification link.
   */
  public void sendUserVerificationEmail(User user) throws MessagingException {
    Context context = new Context();
    context.setVariable("userName", user.getPerson().getName());
    context.setVariable("verificationLink",
        verificationUrlService.generateVerificationUrl(user.getEmail()));
    context.setVariable("supportMailbox", springConfig.mail().username());
    String content = templateEngine.process(VERIFICATION_EMAIL_TEMPLATE, context);
    emailService.sendEmailWithRetries(springConfig.mail().maxRetries(), VERIFICATION_EMAIL_SUBJECT,
        user.getEmail(), content);
  }
}
