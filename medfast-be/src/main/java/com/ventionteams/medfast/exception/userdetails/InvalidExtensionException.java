package com.ventionteams.medfast.exception.userdetails;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when uploading user's photo fails due to invalid extension.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidExtensionException extends RuntimeException {

  public InvalidExtensionException() {
    super("Upload a file with the following extensions: jpg, gif, png, jpeg");
  }

}
