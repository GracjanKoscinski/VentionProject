package com.ventionteams.medfast.exception.auth;

/**
 * Exception that is thrown when the account gets deactivated and user tries to log-in.
 */
public class AccountDeactivatedException extends RuntimeException {

  public AccountDeactivatedException() {
    super("The account got deactivated by admin.");
  }

}
