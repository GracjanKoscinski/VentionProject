package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an email cannot be sent.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSendException extends RuntimeException {

  public EmailSendException() {
    super("Email was not sent");
  }

}
