package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when verification token is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidVerificationTokenException extends RuntimeException {

  public InvalidVerificationTokenException(String userCredential) {
    super(String.format("Verification token for user with credential %s is invalid.",
        userCredential));
  }
}
