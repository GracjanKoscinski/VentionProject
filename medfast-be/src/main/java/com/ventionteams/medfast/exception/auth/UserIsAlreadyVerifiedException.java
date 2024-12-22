package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the user is already verified.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserIsAlreadyVerifiedException extends RuntimeException {

  public UserIsAlreadyVerifiedException(String userCredential) {
    super(String.format("User with credential %s is already verified", userCredential));
  }
}
