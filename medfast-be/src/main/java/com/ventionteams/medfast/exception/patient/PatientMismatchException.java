package com.ventionteams.medfast.exception.patient;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a patient doesn't own appointment.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PatientMismatchException extends RuntimeException {

  public PatientMismatchException(String credential, Long appointmentId) {
    super(String.format("Patient with credential %s doesn't own appointment with id %d",
        credential, appointmentId));
  }
}
