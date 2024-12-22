package com.ventionteams.medfast.exception.userdetails;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when updating doctors address info which don't exist.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UpdatingNotExistingFieldsException extends RuntimeException {
  public UpdatingNotExistingFieldsException() {
    super("Updating fields that don't exist");
  }
}
