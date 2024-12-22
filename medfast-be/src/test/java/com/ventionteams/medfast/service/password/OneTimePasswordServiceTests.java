package com.ventionteams.medfast.service.password;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Checks the one time password service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class OneTimePasswordServiceTests {

  @Mock
  private OneTimePasswordRepository oneTimePasswordRepository;

  @Mock
  private TokenConfig tokenConfig;

  @Mock
  private Random random;

  @Mock
  private UserService userService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private OneTimePasswordService oneTimePasswordService;

  @Test
  public void verify_InvalidToken_ExceptionThrown() {
    String email = "test@example.com";
    String otpToken = "1234";

    when(oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)).thenReturn(
        Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> oneTimePasswordService.verify(email, otpToken));
  }

  @Test
  public void verify_ExpiredToken_TimeoutExceptionThrown() {
    String email = "test@example.com";
    String otpToken = "1234";
    OneTimePassword otp = new OneTimePassword();
    otp.setCreatedDate(LocalDateTime.now().minusSeconds(3600));

    when(oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)).thenReturn(
        Optional.of(otp));
    when(tokenConfig.timeout()).thenReturn(new TokenConfig.Timeout(0, 0, 60));

    assertThrows(TokenExpiredException.class, () -> oneTimePasswordService.verify(email, otpToken));
  }

  @Test
  public void verify_ValidToken_TokenVerified() {
    String email = "test@example.com";
    String otpToken = "1234";
    OneTimePassword otp = new OneTimePassword();
    otp.setCreatedDate(LocalDateTime.now().minusSeconds(30));

    when(oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)).thenReturn(
        Optional.of(otp));
    when(tokenConfig.timeout()).thenReturn(new TokenConfig.Timeout(0, 0, 60));

    oneTimePasswordService.verify(email, otpToken);

    verify(oneTimePasswordRepository, never()).delete(any(OneTimePassword.class));
  }

  @Test
  public void generate_ValidUser_OtpGenerated() {
    User user = new User();
    when(random.nextInt(10000)).thenReturn(1234);

    OneTimePassword otp = oneTimePasswordService.generate(user);

    assertNotNull(otp);
    assertEquals(user, otp.getUser());
    assertEquals("1234", otp.getToken());
    verify(oneTimePasswordRepository, times(1)).save(any(OneTimePassword.class));
  }

  @Test
  public void sendTemporaryPasswordEmail_Success() throws MessagingException {
    String email = "user@example.com";
    String expectedToken = "2345";
    User user = new User();
    OneTimePassword token = new OneTimePassword();
    token.setToken(expectedToken);

    when(userService.findByEmail(email)).thenReturn(user);
    OneTimePasswordService spyService = spy(oneTimePasswordService);
    when(spyService.generate(user)).thenReturn(token);

    String pwd = "temporaryPassword";
    spyService.sendTemporaryPasswordEmail(email, pwd);

    verify(userService).findByEmail(anyString());
    verify(emailService).sendTemporaryPasswordEmail(user, pwd, expectedToken);
  }

  @Test
  public void sendResetPasswordEmail_InvalidEmail_ThrowsException() {
    String email = "test@example.com";

    when(userService.findByEmail(anyString())).thenThrow(
        new UsernameNotFoundException("User with credential " + email + " not found."));

    assertThrows(UsernameNotFoundException.class,
        () -> oneTimePasswordService.sendResetPasswordEmail(email));
  }

  @Test
  public void sendResetPasswordEmail_CorrectEmail_ThrowsException() throws MessagingException {
    String email = "test@example.com";
    User user = User.builder().email(email).build();
    OneTimePassword mockOtp = new OneTimePassword();
    mockOtp.setToken("1111");

    when(userService.findByEmail(email)).thenReturn(user);
    when(oneTimePasswordRepository.save(any(OneTimePassword.class))).thenReturn(mockOtp);
    when(random.nextInt(10000)).thenReturn(1111);

    oneTimePasswordService.sendResetPasswordEmail(email);

    verify(emailService, times(1))
        .sendResetPasswordEmail(eq(user), eq("1111"));
  }
}
