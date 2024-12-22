package com.ventionteams.medfast.service.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.config.properties.TokenConfig.Signing;
import com.ventionteams.medfast.config.properties.TokenConfig.Timeout;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks jwt service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

  @Mock
  private TokenConfig tokenConfig;

  @InjectMocks
  private JwtService jwtService;

  private Timeout timeout;
  private Signing signing;
  String signingKey = "1270b9e6a02e8930e397242c952ab49e531b9d3c680750e65825eb9bd2a05141";

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(signingKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @BeforeEach
  void setUpMocks() {
    timeout = mock(Timeout.class);
    signing = mock(Signing.class);

    when(tokenConfig.timeout()).thenReturn(timeout);
    when(tokenConfig.signing()).thenReturn(signing);
    when(signing.key()).thenReturn(signingKey);
  }

  @Test
  public void generateToken_ValidUser_TokenGenerated() {
    User user = User.builder().id(1L).email("test@example.com").role(Role.PATIENT).build();

    when(timeout.access()).thenReturn(3600L);

    String token = jwtService.generateToken(user);

    Claims claims = Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Assertions.assertNotNull(token);
    Assertions.assertFalse(token.isEmpty());
    Assertions.assertEquals(1, claims.get("id"));
    Assertions.assertEquals("test@example.com", claims.get("email"));
    Assertions.assertEquals("PATIENT", claims.get("role"));
  }

  @Test
  public void isTokenValid_InvalidUser_ReturnsFalse() {
    User user = User.builder().id(1L).email("test@example.com").role(Role.PATIENT).build();
    User otherUser = User.builder().id(2L).email("other@example.com").role(Role.PATIENT).build();

    when(timeout.access()).thenReturn(3600L);

    String token = jwtService.generateToken(user);

    boolean isValid = jwtService.isTokenValid(token, otherUser);

    Assertions.assertFalse(isValid);
  }

  @Test
  public void isTokenValid_ExpiredToken_ReturnsFalse() {
    User user = User.builder().id(1L).email("test@example.com").role(Role.PATIENT).build();

    when(timeout.access()).thenReturn(0L);

    String token = jwtService.generateToken(user);

    assertThrows(ExpiredJwtException.class, () ->
        jwtService.isTokenValid(token, user));

  }

  @Test
  public void isTokenValid_ValidToken_ReturnsTrue() {
    User user = User.builder().id(1L).email("test@example.com").role(Role.PATIENT).build();

    when(timeout.access()).thenReturn(3600L);

    String token = jwtService.generateToken(user);

    boolean isValid = jwtService.isTokenValid(token, user);

    Assertions.assertTrue(isValid);
  }

  @Test
  void extractUserName_ValidToken_ReturnsUserName() {
    User user = User.builder().id(1L).email("test@example.com").role(Role.PATIENT).build();
    ;
    when(timeout.access()).thenReturn(3600L);
    when(tokenConfig.signing()).thenReturn(signing);

    String token = jwtService.generateToken(user);

    String extractedUserName = jwtService.extractUserName(token);

    Assertions.assertEquals(user.getUsername(), extractedUserName);
  }
}
