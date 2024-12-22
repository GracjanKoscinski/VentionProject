package com.ventionteams.medfast.exception.filesystem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when operations like save or get file fails.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileOperationException extends RuntimeException {
  public FileOperationException(String message) {
    super(String.format("Failed with: %s", message));
  }

}
