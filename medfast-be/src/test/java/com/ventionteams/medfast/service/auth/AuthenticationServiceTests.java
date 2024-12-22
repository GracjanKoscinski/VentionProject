package com.ventionteams.medfast.service.auth;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.config.properties.TokenConfig.Timeout;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.exception.auth.AccountDeactivatedException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Checks authentication service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private EmailVerificationService emailVerificationService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private TokenConfig tokenConfig;

  @Mock
  private User mockedUser;

  @Mock
  private UserDetailsService mockedUserDetailsService;

  SignInRequest signInRequest = new SignInRequest(
      "test@example.com", "qweRTY123$");

  @InjectMocks
  private AuthenticationService authenticationService;

  @Test
  public void signUp_EmailAuthenticationFails_ExceptionThrown() throws MessagingException {
    PatientRegistrationRequest request = PatientRegistrationRequest.builder()
        .email("user@example.com")
        .password("passwrod")
        .name("John")
        .surname("Doe")
        .birthDate(LocalDate.now())
        .streetAddress("Main street")
        .house("123")
        .apartment("42 a")
        .city("Chicago")
        .citizenship("Illinios")
        .zip("60007")
        .phone("12345678900")
        .sex(Gender.MALE)
        .state("Canada")
        .checkboxTermsAndConditions(true)
        .build();
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.findByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doThrow(MailAuthenticationException.class)
        .when(emailVerificationService).sendUserVerificationEmail(user);

    Assertions.assertThrows(MailAuthenticationException.class,
        () -> authenticationService.signUp(request));
    verify(emailVerificationService).sendUserVerificationEmail(user);
  }

  @Test
  public void signUp_UserAlreadyEnabled_ExceptionThrown() {
    PatientRegistrationRequest request = PatientRegistrationRequest.builder()
        .email("user@example.com")
        .password("passwrod")
        .name("John")
        .surname("Doe")
        .birthDate(LocalDate.now())
        .streetAddress("Main street")
        .house("123")
        .apartment("42 a")
        .city("Chicago")
        .citizenship("Illinios")
        .zip("60007")
        .phone("12345678900")
        .sex(Gender.MALE)
        .state("Canada")
        .checkboxTermsAndConditions(true)
        .build();
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(true).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.findByEmail(email)).thenReturn(user);

    Assertions.assertThrows(UserIsAlreadyVerifiedException.class,
        () -> authenticationService.signUp(request));
  }

  @Test
  public void signUp_NoUserExists_UserCreated() throws MessagingException, IOException {
    PatientRegistrationRequest request = PatientRegistrationRequest.builder()
        .email("user@example.com")
        .password("passwrod")
        .name("John")
        .surname("Doe")
        .birthDate(LocalDate.now())
        .streetAddress("Main street")
        .house("123")
        .apartment("42 a")
        .city("Chicago")
        .citizenship("Illinios")
        .zip("60007")
        .phone("12345678900")
        .sex(Gender.MALE)
        .state("Canada")
        .checkboxTermsAndConditions(true)
        .build();
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.findByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doNothing().when(emailVerificationService).sendUserVerificationEmail(user);

    String response = authenticationService.signUp(request);

    Assertions.assertEquals("Email verification link has been sent to your email", response);
    verify(userService).create(request);
    verify(emailVerificationService).sendUserVerificationEmail(user);
  }

  @Test
  public void sendVerificationEmail_UserWithEmailNoExists_ExceptionThrown() {
    String email = "invalid@example.com";

    when(userService.findByEmail(email)).thenThrow(UsernameNotFoundException.class);

    Assertions.assertThrows(UsernameNotFoundException.class,
        () -> authenticationService.sendVerificationEmail(email));
  }

  @Test
  public void sendVerificationEmail_UserAlreadyEnabled_ExceptionThrown() {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(true).build();

    when(userService.findByEmail(email)).thenReturn(user);

    Assertions.assertThrows(UserIsAlreadyVerifiedException.class,
        () -> authenticationService.sendVerificationEmail(email));
  }

  @Test
  public void sendVerificationEmail_EmailAuthenticationFails_ExceptionThrown()
      throws MessagingException {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.findByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doThrow(MailAuthenticationException.class)
        .when(emailVerificationService).sendUserVerificationEmail(user);

    Assertions.assertThrows(MailAuthenticationException.class,
        () -> authenticationService.sendVerificationEmail(email));
    verify(emailVerificationService).sendUserVerificationEmail(user);
  }

  @Test
  public void sendVerificationEmail_ValidEmail_SendEmail() throws MessagingException, IOException {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.findByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doNothing().when(emailVerificationService).sendUserVerificationEmail(user);

    authenticationService.sendVerificationEmail(email);

    verify(userService).findByEmail(email);
    verify(verificationTokenService).addVerificationTokenForUser(email);
    verify(emailVerificationService).sendUserVerificationEmail(user);
  }

  @Test
  public void signIn_InvalidUsernamePassword_ExceptionThrown() {
    SignInRequest request = new SignInRequest("test@example.com", "qweRTY123$");

    doThrow(BadCredentialsException.class).when(authenticationManager)
        .authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));

    Assertions.assertThrows(AuthenticationException.class,
        () -> authenticationService.signIn(request));
  }

  @Test
  public void signIn_CredentialsExpired_ExceptionThrown() {
    SignInRequest request = new SignInRequest("test@example.com", "qweRTY123$");

    doThrow(CredentialsExpiredException.class).when(authenticationManager)
        .authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));

    Assertions.assertThrows(CredentialsExpiredException.class,
        () -> authenticationService.signIn(request));
  }

  @Test
  public void singIn_GoodCredentials_ReturnsJwtResponse() {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(mockedUser);
    refreshToken.setToken("exampleRefreshToken");
    UserDetailsService userDetailsService = mock(UserDetailsService.class);
    Timeout timeout = mock(Timeout.class);

    when(userService.getUserDetailsService()).thenReturn(userDetailsService);
    when(userDetailsService.loadUserByUsername(signInRequest.getEmail())).thenReturn(mockedUser);
    when(jwtService.generateToken(mockedUser)).thenReturn("exampleToken");
    when(refreshTokenService.generateToken(mockedUser)).thenReturn(refreshToken);
    when(tokenConfig.timeout()).thenReturn(timeout);
    when(mockedUser.getUserStatus()).thenReturn(UserStatus.ACTIVE);
    when(timeout.access()).thenReturn(3600L);
    when(timeout.refresh()).thenReturn(7200L);

    JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

    Assertions.assertEquals("exampleToken", response.getAccessToken());
    Assertions.assertEquals("exampleRefreshToken", response.getRefreshToken());
  }

  @Test
  public void singIn_DeactivatedAccount_ExceptionThrown() {
    when(userService.getUserDetailsService()).thenReturn(mockedUserDetailsService);
    when(mockedUserDetailsService.loadUserByUsername(signInRequest.getEmail()))
        .thenReturn(mockedUser);
    when(mockedUser.getUserStatus()).thenReturn(UserStatus.DEACTIVATED);

    assertThrows(AccountDeactivatedException.class, () ->
        authenticationService.signIn(signInRequest));

    verify(jwtService, never()).generateToken(any());
    verify(refreshTokenService, never()).generateToken(any());
  }

  @Test
  public void signIn_DeletedAccount_ExceptionThrown() {
    when(userService.getUserDetailsService()).thenReturn(mockedUserDetailsService);
    when(mockedUserDetailsService.loadUserByUsername(signInRequest.getEmail()))
        .thenReturn(mockedUser);
    when(mockedUser.getUserStatus()).thenReturn(UserStatus.DELETED);

    assertThrows(AccountDeactivatedException.class, () ->
        authenticationService.signIn(signInRequest));

    verify(jwtService, never()).generateToken(any());
    verify(refreshTokenService, never()).generateToken(any());
  }
}