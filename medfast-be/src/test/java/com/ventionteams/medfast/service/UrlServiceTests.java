package com.ventionteams.medfast.service;

import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.service.auth.VerificationTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Checks service for generating urls with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class UrlServiceTests {

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  AppConfig appConfig;

  @InjectMocks
  private UrlService urlService;

  @Test
  public void generateVerificationUrl_InvalidEmail_ExceptionThrown() {
    String email = "invalid";

    when(verificationTokenService.getVerificationTokenByUserEmail(email)).thenThrow(
        EntityNotFoundException.class);

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> urlService.generateVerificationUrl(email));
  }

  @Test
  public void generateVerificationUrl_ValidEmail_ReturnsCorrectUrl() {
    String email = "test@example.com";
    String baseUrl = "http://localhost:8080";
    String token = "exampleToken";
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setId(1L);
    verificationToken.setToken("exampleToken");
    verificationToken.setUser(null);
    String expectedUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .path("/verify")
        .queryParam("email", email)
        .queryParam("code", token)
        .toUriString();

    when(verificationTokenService.getVerificationTokenByUserEmail(email)).thenReturn(
        verificationToken);
    when(appConfig.baseUrl()).thenReturn(baseUrl);

    String url = urlService.generateVerificationUrl(email);

    Assertions.assertEquals(expectedUrl, url);
  }

  @Test
  public void generateSetPermanentPasswordUrl_EmailAndCode_ReturnsCorrectUrl() {
    String email = "test@example.com";
    String baseUrl = "http://localhost:8080";
    String code = "1234";
    String expectedUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .path("/set-permanent-password")
        .queryParam("email", email)
        .queryParam("code", code)
        .toUriString();

    when(appConfig.baseUrl()).thenReturn(baseUrl);

    String url = urlService.generateSetPermanentPasswordUrl(email, code);

    Assertions.assertEquals(expectedUrl, url);
  }
}
