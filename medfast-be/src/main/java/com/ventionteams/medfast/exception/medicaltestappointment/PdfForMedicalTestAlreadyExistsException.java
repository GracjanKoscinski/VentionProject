package com.ventionteams.medfast.exception.medicaltestappointment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the medical test data is invalid.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PdfForMedicalTestAlreadyExistsException extends RuntimeException {
  public PdfForMedicalTestAlreadyExistsException(Long testAppointmentId) {
    super(String.format("A PDF already exists for the medical test with ID %d. "
        + "No new PDF can be generated.", testAppointmentId));
  }
}
