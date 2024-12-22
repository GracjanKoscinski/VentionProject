package com.ventionteams.medfast.service.password;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling one-time password operations.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class OneTimePasswordService {

  private final OneTimePasswordRepository oneTimePasswordRepository;
  private final EmailService emailService;
  private final UserService userService;
  private final Random random;
  private final TokenConfig tokenConfig;

  /**
   * Verify the one-time password token.
   */
  public void verify(String email, String otpToken) {
    OneTimePassword otp = oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)
        .orElseThrow(
            () -> new EntityNotFoundException(OneTimePassword.class, email));

    long actualValidityPeriod = Duration.between(otp.getCreatedDate(), LocalDateTime.now())
        .getSeconds();
    if (actualValidityPeriod > tokenConfig.timeout().resetPassword()) {
      oneTimePasswordRepository.delete(otp);
      throw new TokenExpiredException(otpToken);
    }
  }

  /**
   * Send a reset password email to the user.
   */
  public void sendResetPasswordEmail(String email) throws MessagingException {
    User user = userService.findByEmail(email);
    emailService.sendResetPasswordEmail(user, generate(user).getToken());
  }

  /**
   * Send an email with temporary password and a link to 'set permanent password page'.
   */
  public void sendTemporaryPasswordEmail(String email, String pwd) throws MessagingException {
    User user = userService.findByEmail(email);
    emailService.sendTemporaryPasswordEmail(user, pwd, generate(user).getToken());
  }

  /**
   * Generate a 4-digit one-time password token for the user.
   */
  public OneTimePassword generate(User user) {
    String otpToken = String.format("%04d", random.nextInt(10000));
    OneTimePassword otp = new OneTimePassword();
    otp.setUser(user);
    otp.setToken(otpToken);
    oneTimePasswordRepository.save(otp);
    log.info("Generated one-time password for the user with id {}", user.getId());
    return otp;
  }

}
