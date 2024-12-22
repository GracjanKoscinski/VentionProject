package com.ventionteams.medfast.enums;

/**
 * Enum that represents consultation appointment type.
 */
public enum ConsultationAppointmentType {
  ONLINE("Online"),
  ONSITE("On-Site");

  private final String value;

  ConsultationAppointmentType(String toString) {
    this.value = toString;
  }

  @Override
  public String toString() {
    return value;
  }
}
