package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.VerificationConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.auth.InvalidVerificationTokenException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.repository.VerificationTokenRepository;
import com.ventionteams.medfast.service.UserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for the user verification token entity.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class VerificationTokenService {

  private final VerificationTokenRepository verificationTokenRepository;
  private final UserService userService;
  private final VerificationConfig verificationConfig;

  /**
   * Adds a verification token for the user with the given email.
   */
  @Transactional
  public void addVerificationTokenForUser(String email) {
    UUID securityCode = UUID.randomUUID();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(securityCode.toString());
    verificationToken.setUser(userService.findByEmail(email));
    verificationTokenRepository.save(verificationToken);
  }

  /**
   * Verifies the user with the given email and verification token.
   */
  @Transactional
  public void verify(String email, String verificationToken) {
    User user = userService.findByEmail(email);
    if (user.isEnabled()) {
      deleteVerificationToken(email);
      throw new UserIsAlreadyVerifiedException(email);
    }

    if (!validateToken(email, verificationToken)) {
      throw new InvalidVerificationTokenException(email);
    }

    user.setEnabled(true);
    user.setUserStatus(UserStatus.ACTIVE);
    userService.save(user);
    deleteVerificationToken(email);
    log.info("The user with id {} has been verified", user.getId());
  }

  private boolean validateToken(String email, String verificationToken) {
    VerificationToken token = getVerificationTokenByUserEmail(email);
    long actualValidityPeriod = Duration.between(token.getCreatedDate(), LocalDateTime.now())
        .getSeconds();
    if (actualValidityPeriod > verificationConfig.code().timeout()) {
      deleteVerificationToken(email);
      return false;
    }

    return token.getToken().equals(verificationToken);
  }

  /**
   * Seeks for verification token by email.
   */
  public VerificationToken getVerificationTokenByUserEmail(String email) {
    return verificationTokenRepository.findByUserEmail(email)
        .orElseThrow(
            () -> new EntityNotFoundException(VerificationToken.class, email));
  }

  public void deleteVerificationToken(String email) {
    VerificationToken token = getVerificationTokenByUserEmail(email);
    verificationTokenRepository.deleteById(token.getId());
  }
}
