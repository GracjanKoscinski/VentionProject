package com.ventionteams.medfast.exception.auth.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when current password is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCurrentPasswordException extends RuntimeException {

  public InvalidCurrentPasswordException(String userCredential) {
    super(String.format("User with credential %s provided wrong current password.",
        userCredential));
  }
}
