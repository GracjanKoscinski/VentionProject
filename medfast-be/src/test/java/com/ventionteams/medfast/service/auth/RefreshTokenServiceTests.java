package com.ventionteams.medfast.service.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.config.properties.TokenConfig.Timeout;
import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Checks refresh token service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private JwtService jwtService;

  @Mock
  private TokenConfig tokenConfig;

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @Test
  public void generateToken_InvalidEmail_ExceptionThrown() {
    UserDetails user = mock(UserDetails.class);
    String email = "invalid@example.com";

    when(user.getUsername()).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    Assertions.assertThrows(UsernameNotFoundException.class,
        () -> refreshTokenService.generateToken(user));
  }

  @Test
  public void generateToken_ValidEmail_ReturnsToken() {
    String email = "test@example.com";
    User user = User.builder().email(email).build();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    RefreshToken token = refreshTokenService.generateToken(user);

    Assertions.assertNotNull(token);
    Assertions.assertEquals(user, token.getUser());
    Assertions.assertNotNull(token.getToken());
  }

  @Test
  public void refreshToken_InvalidToken_ExceptionThrown() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("550e8400-e29b-41d4-a716-446655440000");

    when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

    Assertions.assertThrows(NoSuchElementException.class,
        () -> refreshTokenService.refreshToken(request));
  }

  @Test
  public void refreshToken_TokenExpired_ExceptionThrown() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("550e8400-e29b-41d4-a716-446655440000");
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken("550e8400-e29b-41d4-a716-446655440000");
    refreshToken.setCreatedDate(LocalDateTime.now().minusSeconds(7201));
    Timeout timeout = mock(Timeout.class);

    when(tokenConfig.timeout()).thenReturn(timeout);
    when(timeout.refresh()).thenReturn(7200L);
    when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));

    Assertions.assertThrows(TokenExpiredException.class,
        () -> refreshTokenService.refreshToken(request));
  }

  @Test
  public void refreshToken_ValidRequest_ReturnsJwtAuthenticationResponse() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("550e8400-e29b-41d4-a716-446655440000");
    RefreshToken refreshToken = new RefreshToken();
    User user = new User();
    refreshToken.setToken("550e8400-e29b-41d4-a716-446655440000");
    refreshToken.setUser(user);
    refreshToken.setCreatedDate(LocalDateTime.now().minusSeconds(100));
    Timeout timeout = mock(Timeout.class);

    when(tokenConfig.timeout()).thenReturn(timeout);
    when(timeout.refresh()).thenReturn(7200L);
    when(timeout.access()).thenReturn(3600L);
    when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
    when(jwtService.generateToken(user)).thenReturn(
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj");

    JwtAuthenticationResponse response = refreshTokenService.refreshToken(request);

    Assertions.assertNotNull(response);
    Assertions.assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj",
        response.getAccessToken());
    Assertions.assertEquals("550e8400-e29b-41d4-a716-446655440000", response.getRefreshToken());
  }
}
