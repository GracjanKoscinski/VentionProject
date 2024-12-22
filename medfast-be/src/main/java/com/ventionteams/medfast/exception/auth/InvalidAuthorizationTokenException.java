package com.ventionteams.medfast.exception.auth;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown if no Authorization header or it has wrong format.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidAuthorizationTokenException extends JwtException {

  public InvalidAuthorizationTokenException() {
    super("Authorization token is missing or invalid.");
  }
}
