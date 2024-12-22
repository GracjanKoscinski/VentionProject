package com.ventionteams.medfast.exception.appointment;

import com.ventionteams.medfast.enums.TimeType;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when desired time an appointment is in the past.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAppointmentTimeException extends RuntimeException {

  public InvalidAppointmentTimeException(LocalDateTime from, TimeType time) {
    super(String.format("Invalid appointment time: %s is in the %s", from, time));
  }
}
