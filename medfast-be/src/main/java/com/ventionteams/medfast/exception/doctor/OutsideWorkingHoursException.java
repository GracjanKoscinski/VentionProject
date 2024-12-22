package com.ventionteams.medfast.exception.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown patient is trying to schedule a visit outside working hours.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutsideWorkingHoursException extends RuntimeException {

  public OutsideWorkingHoursException(String message) {
    super(message);
  }
}
