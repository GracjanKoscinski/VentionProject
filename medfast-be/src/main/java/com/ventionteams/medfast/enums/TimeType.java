package com.ventionteams.medfast.enums;

/**
 * Enum for the time.
 */
public enum TimeType {
  PAST("Past"),
  CURRENT("Current time"),
  FUTURE("Future");

  private final String value;

  TimeType(String toString) {
    this.value = toString;
  }

  @Override
  public String toString() {
    return value;
  }
}

