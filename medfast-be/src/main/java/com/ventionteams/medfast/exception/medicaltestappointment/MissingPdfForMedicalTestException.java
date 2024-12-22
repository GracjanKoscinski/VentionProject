package com.ventionteams.medfast.exception.medicaltestappointment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the medical test data is invalid.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingPdfForMedicalTestException extends RuntimeException {
  
  public MissingPdfForMedicalTestException(Long id)  {
    super(String.format("No PDF found for medical test with id %s.", id));
  }
}
