package com.ventionteams.medfast.exception.appointment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the amount of appointments is negative.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NegativeAppointmentsAmountException extends RuntimeException {

  public NegativeAppointmentsAmountException(Long userId, int appointmentAmount) {
    super(String.format("Failed to get appointments for the user with id %d,"
            + " appointmentAmount %d < 0.", userId, appointmentAmount));
  }
}
