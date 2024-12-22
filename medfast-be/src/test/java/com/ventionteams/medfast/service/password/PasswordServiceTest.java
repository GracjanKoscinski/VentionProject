package com.ventionteams.medfast.service.password;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.dto.request.SetPermanentPasswordRequest;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.auth.TermsAndConditionsNotAcceptedException;
import com.ventionteams.medfast.exception.auth.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.auth.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.exception.auth.password.PermanentPasswordAlreadySetException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Checks the reset password service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private OneTimePasswordRepository oneTimePasswordRepository;

  @InjectMocks
  private PasswordService passwordService;

  @Test
  public void resetPassword_ValidToken_NewPasswordNotInHistory() {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setEmail("test@example.com");
    request.setOtp("1234");
    request.setNewPassword("newPassword");

    User user = new User();
    user.setPassword("oldPassword");

    OneTimePassword otp = new OneTimePassword();
    otp.setUser(user);

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.of(otp));
    when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);

    passwordService.resetPassword(request);

    verify(userService, times(1)).resetPassword(user,
        passwordEncoder.encode(request.getNewPassword()));
  }

  @Test
  public void resetPassword_TokenNotFound_ExceptionThrown() {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setEmail("test@example.com");
    request.setOtp("1234");
    request.setNewPassword("newPassword");

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> passwordService.resetPassword(request));
  }

  @Test
  public void resetPassword_NewPasswordMatchesOldPassword_ExceptionThrown() {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setEmail("test@example.com");
    request.setOtp("1234");
    request.setNewPassword("newPassword");

    User user = new User();
    user.setPassword("newPassword");

    OneTimePassword otp = new OneTimePassword();
    otp.setUser(user);

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.of(otp));
    when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

    assertThrows(PasswordDoesNotMeetRepetitionConstraint.class,
        () -> passwordService.resetPassword(request));
  }

  @Test
  public void changePassword_InvalidCurrentPassword_ExceptionThrown() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");

    User user = createTestUser(Role.PATIENT, "123123123", true);

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        false);

    assertThrows(InvalidCurrentPasswordException.class,
        () -> passwordService.changePassword(user, request));
  }

  @Test
  public void changePassword_NewPasswordEqualsCurrentPassword_ExceptionThrown() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("password");
    request.setNewPassword("password");

    User user = createTestUser(Role.PATIENT, "password", true);

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        true);
    userService.resetPassword(user,
        passwordEncoder.encode(request.getNewPassword()));

    assertThrows(PasswordDoesNotMeetRepetitionConstraint.class,
        () -> passwordService.changePassword(user, request));
  }

  @Test
  public void changePassword_TermsAndConditionsNotAccepted_ExceptionThrown() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");

    User user = createTestUser(Role.PATIENT, "currentPassword", true);

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        true);

    passwordService.changePassword(user, request);

    verify(userService, never()).resetPassword(eq(user), anyString());
  }

  @Test
  public void changePassword_CorrectRequest_resetPasswordInvoked() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");

    User user = createTestUser(Role.PATIENT, "currentPassword", true);

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        true);

    passwordService.changePassword(user, request);

    verify(userService, times(1)).resetPassword(user,
        passwordEncoder.encode(request.getNewPassword()));
  }

  @Test
  public void generatePassword_ReturnSecurePassword() {
    String password = passwordService.generatePassword();

    boolean hasDigit = false;
    boolean hasSpecialChar = false;
    boolean hasLowercase = false;
    boolean hasUppercase = false;
    boolean hasNoWhitespace = true;

    for (char c : password.toCharArray()) {
      if (Character.isDigit(c)) {
        hasDigit = true;
      } else if (c >= 33 && c <= 47
          || c >= 58 && c <= 64
          || c >= 91 && c <= 96
          || c >= 123 && c <= 126) {
        hasSpecialChar = true;
      } else if (Character.isLowerCase(c)) {
        hasLowercase = true;
      } else if (Character.isUpperCase(c)) {
        hasUppercase = true;
      } else if (Character.isWhitespace(c)) {
        hasNoWhitespace = false;
      }
    }

    assertTrue("Password must contain at least one digit", hasDigit);
    assertTrue("Password must contain at least one special character", hasSpecialChar);
    assertTrue("Password must contain at least one lowercase letter", hasLowercase);
    assertTrue("Password must contain at least one uppercase letter", hasUppercase);
    assertTrue("Password must not contain whitespace", hasNoWhitespace);
  }

  @Test
  void setPermanentPassword_TokenNotFound_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("invalidCode");

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        passwordService.setPermanentPassword(request)
    );

  }

  @Test
  void setPermanentPassword_UserNotFound_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");

    when(userService.findByEmail(request.getEmail()))
        .thenThrow(UsernameNotFoundException.class);

    assertThrows(UsernameNotFoundException.class, () ->
        passwordService.setPermanentPassword(request)
    );
  }

  @Test
  void setPermanentPassword_AccessDenied_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");
    request.setNewPassword("newPassword");
    request.setCheckboxTermsAndConditions(true);

    User user = new User();
    user.setRole(Role.PATIENT);

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.of(new OneTimePassword()));
    when(userService.findByEmail(request.getEmail())).thenReturn(user);

    assertThrows(AccessDeniedException.class, () ->
        passwordService.setPermanentPassword(request)
    );

  }

  @Test
  void setPermanentPassword_PermanentPasswordAlreadySet_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");
    request.setNewPassword("newPassword");
    request.setCheckboxTermsAndConditions(true);

    User user = createTestUser(Role.DOCTOR, "currentPassword", true);

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.of(new OneTimePassword()));
    when(userService.findByEmail(request.getEmail())).thenReturn(user);

    assertThrows(PermanentPasswordAlreadySetException.class, () ->
        passwordService.setPermanentPassword(request)
    );
  }

  @Test
  void setPermanentPassword_PasswordRepetitionConstraint_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");
    request.setNewPassword("currentPassword");
    request.setCheckboxTermsAndConditions(true);

    User user = createTestUser(Role.DOCTOR, "currentPassword", false);

    when(passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
        .thenReturn(true);
    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.of(new OneTimePassword()));
    when(userService.findByEmail(request.getEmail())).thenReturn(user);

    assertThrows(PasswordDoesNotMeetRepetitionConstraint.class, () ->
        passwordService.setPermanentPassword(request)
    );
  }

  @Test
  void setPermanentPassword_TermsAndConditionsNotAccepted_ExceptionThrown() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");
    request.setNewPassword("newPassword");
    request.setCheckboxTermsAndConditions(false);

    User user = createTestUser(Role.DOCTOR, "currentPassword", false);

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.of(new OneTimePassword()));
    when(userService.findByEmail(request.getEmail())).thenReturn(user);

    assertThrows(TermsAndConditionsNotAcceptedException.class, () ->
        passwordService.setPermanentPassword(request)
    );
  }

  @Test
  void setPermanentPassword_CorrectRequest_Success() {
    SetPermanentPasswordRequest request = new SetPermanentPasswordRequest();
    request.setEmail("user@example.com");
    request.setCode("validCode");
    request.setNewPassword("newPassword");
    request.setCheckboxTermsAndConditions(true);

    User user = createTestUser(Role.DOCTOR, "currentPassword", false);

    OneTimePassword otp = new OneTimePassword();

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getCode()))
        .thenReturn(Optional.of(otp));
    when(userService.findByEmail(request.getEmail())).thenReturn(user);
    when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedPassword");

    passwordService.setPermanentPassword(request);

    verify(userService).save(user);
    verify(oneTimePasswordRepository).deleteByUserEmailAndToken(
        request.getEmail(), request.getCode());
  }

  private User createTestUser(Role role, String password, Boolean termsAndConditions) {
    Person person = Person.builder()
        .build();
    return User.builder()
        .role(role)
        .password(password)
        .checkboxTermsAndConditions(termsAndConditions)
        .person(person)
        .build();
  }
}
