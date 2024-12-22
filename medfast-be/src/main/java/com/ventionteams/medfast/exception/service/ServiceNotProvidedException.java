package com.ventionteams.medfast.exception.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when doctor doesn't provide specific service.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ServiceNotProvidedException extends RuntimeException {

  public ServiceNotProvidedException(Long doctorId, Long serviceId) {
    super(String.format("Doctor with id %d doesn't provide service with id %s",
        doctorId, serviceId));
  }
}
