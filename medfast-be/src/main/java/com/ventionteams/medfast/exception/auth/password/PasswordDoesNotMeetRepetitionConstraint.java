package com.ventionteams.medfast.exception.auth.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the password does not meet the repetition constraint.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordDoesNotMeetRepetitionConstraint extends RuntimeException {

  public PasswordDoesNotMeetRepetitionConstraint(String userCredential) {
    super(String.format("Password repetition constraint violated for user with credential %s.",
        userCredential));
  }
}
