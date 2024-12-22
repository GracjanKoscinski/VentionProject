package com.ventionteams.medfast.exception.specialization;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the specialization is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SpecializationsNotFoundException extends RuntimeException {

  public SpecializationsNotFoundException(List<Long> ids) {
    super(String.format("Specializations with IDs %s not found.", ids));
  }
}
