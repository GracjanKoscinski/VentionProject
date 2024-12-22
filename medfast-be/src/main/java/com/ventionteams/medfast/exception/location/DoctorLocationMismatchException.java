package com.ventionteams.medfast.exception.location;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when doctor doesn't work at provided location.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DoctorLocationMismatchException extends RuntimeException {

  public DoctorLocationMismatchException(Long doctorId, Long locationId) {
    super(String.format("Doctor with id %d doesn't work at location with id %d.",
        doctorId, locationId));
  }
}
