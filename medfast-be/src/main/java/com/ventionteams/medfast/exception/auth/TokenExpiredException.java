package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the token sent has already expired.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String token) {
    super(String.format("The token [%s] has already expired", token));
  }
}
