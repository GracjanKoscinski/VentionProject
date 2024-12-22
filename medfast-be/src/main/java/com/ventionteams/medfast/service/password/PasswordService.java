package com.ventionteams.medfast.service.password;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.dto.request.SetPermanentPasswordRequest;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.auth.TermsAndConditionsNotAcceptedException;
import com.ventionteams.medfast.exception.auth.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.auth.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.exception.auth.password.PermanentPasswordAlreadySetException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling reset password operations.
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final OneTimePasswordRepository oneTimePasswordRepository;

  /**
   * Reset the password for the user.
   */
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    OneTimePassword token = oneTimePasswordRepository
        .findByUserEmailAndToken(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())
        .orElseThrow(() -> new EntityNotFoundException(OneTimePassword.class,
            resetPasswordRequest.getEmail()));

    if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(),
        token.getUser().getPassword())) {
      throw new PasswordDoesNotMeetRepetitionConstraint(token.getUser().getEmail());
    }

    userService.resetPassword(token.getUser(),
        passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
  }

  /**
   * Change the password for the logged-in user.
   */
  public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
    if (!user.isCheckboxTermsAndConditions()) {
      throw new TermsAndConditionsNotAcceptedException(user.getEmail());
    }

    if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
      throw new InvalidCurrentPasswordException(user.getEmail());
    }
    if (changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword())) {
      throw new PasswordDoesNotMeetRepetitionConstraint(
          user.getEmail());
    }

    userService.resetPassword(user,
        passwordEncoder.encode(changePasswordRequest.getNewPassword()));
  }

  /**
   * Method which generates secure random password using 'passay'.
   */
  public String generatePassword() {
    CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
    CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
    lowerCaseRule.setNumberOfCharacters(2);

    CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
    CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
    upperCaseRule.setNumberOfCharacters(2);

    CharacterData digitChars = EnglishCharacterData.Digit;
    CharacterRule digitRule = new CharacterRule(digitChars);
    digitRule.setNumberOfCharacters(2);

    CharacterData specialChars = new CharacterData() {
      public String getErrorCode() {
        return ERROR_CODE;
      }

      public String getCharacters() {
        return "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
      }
    };
    CharacterRule splCharRule = new CharacterRule(specialChars);
    splCharRule.setNumberOfCharacters(2);

    PasswordGenerator gen = new PasswordGenerator();

    return gen.generatePassword(10, splCharRule, lowerCaseRule,
        upperCaseRule, digitRule);
  }

  /**
   * Set permanent password for a new doctor.
   */
  @Transactional
  public void setPermanentPassword(SetPermanentPasswordRequest request) {
    User user = userService.findByEmail(request.getEmail());

    oneTimePasswordRepository.findByUserEmailAndToken(
            request.getEmail(), request.getCode())
        .orElseThrow(() -> new EntityNotFoundException(OneTimePassword.class,
            request.getEmail()));

    if (user.getRole() != Role.DOCTOR) {
      throw new AccessDeniedException(
          String.format("User with credential %s and role %s tried "
                  + "to set a permanent password but does not have the necessary permissions.",
              user.getEmail(), user.getRole()));
    }

    if (user.isCheckboxTermsAndConditions()) {
      throw new PermanentPasswordAlreadySetException(user.getEmail());
    }

    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      throw new PasswordDoesNotMeetRepetitionConstraint(user.getEmail());
    }

    if (!request.getCheckboxTermsAndConditions()) {
      throw new TermsAndConditionsNotAcceptedException(user.getEmail());
    }

    user.setPasswordExpirationDate(null);
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setCheckboxTermsAndConditions(
        request.getCheckboxTermsAndConditions()
    );
    userService.save(user);
    oneTimePasswordRepository.deleteByUserEmailAndToken(
        request.getEmail(), request.getCode()
    );
  }
}
