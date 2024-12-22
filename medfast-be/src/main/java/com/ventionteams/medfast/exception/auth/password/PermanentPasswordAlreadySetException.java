package com.ventionteams.medfast.exception.auth.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when permanent password has already been set.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PermanentPasswordAlreadySetException extends RuntimeException {

  public PermanentPasswordAlreadySetException(String userCredential) {
    super(String.format("Permanent password has already been set "
        + "for the user with credential %s", userCredential));
  }
}
