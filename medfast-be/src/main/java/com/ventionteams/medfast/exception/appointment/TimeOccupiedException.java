package com.ventionteams.medfast.exception.appointment;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when desired time of appointment is occupied.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class TimeOccupiedException extends RuntimeException {

  public TimeOccupiedException(LocalDateTime dateFrom) {
    super(String.format("The requested appointment time is already occupied: %s.", dateFrom));
  }
}
