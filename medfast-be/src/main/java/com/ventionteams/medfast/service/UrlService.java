package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.service.auth.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Url generating service.
 */
@Service
@RequiredArgsConstructor
public class UrlService {

  private final VerificationTokenService verificationTokenService;
  private final AppConfig appConfig;

  /**
   * Generates the verification URL for the user with the given email consisting of the email and a
   * verification code.
   */
  public String generateVerificationUrl(String email) {
    VerificationToken verificationToken = verificationTokenService.getVerificationTokenByUserEmail(
        email);
    return UriComponentsBuilder.fromHttpUrl(appConfig.baseUrl())
        .path("/verify")
        .queryParam("email", email)
        .queryParam("code", verificationToken.getToken())
        .toUriString();
  }

  /**
   * Generates set temporary password URL for the user with the given email consisting of the email
   * and a code.
   */
  public String generateSetPermanentPasswordUrl(String email, String code) {
    return UriComponentsBuilder.fromHttpUrl(appConfig.baseUrl())
        .path("/set-permanent-password")
        .queryParam("email", email)
        .queryParam("code", code)
        .toUriString();
  }

}
