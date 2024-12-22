package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when TermsAndConditions are not accepted.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TermsAndConditionsNotAcceptedException extends RuntimeException {

  public TermsAndConditionsNotAcceptedException(String userCredential) {
    super(String.format("Terms and conditions are not accepted by user"
        + " with crential %s", userCredential));
  }
}
