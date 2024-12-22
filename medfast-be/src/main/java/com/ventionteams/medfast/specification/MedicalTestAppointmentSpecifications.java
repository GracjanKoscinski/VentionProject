package com.ventionteams.medfast.specification;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class to handle dynamic query specifications for MedicalTestAppointment entity.
 */
public class MedicalTestAppointmentSpecifications {

  /**
   * Specification to filter appointments by test ID.
   */
  public static Specification<MedicalTestAppointment> hasTestId(Long testId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("test").get("id"), testId);
  }

  /**
   * Specification to filter appointments by location ID.
   */
  public static Specification<MedicalTestAppointment> hasLocationId(Long locationId) {
    return (root, query, criteriaBuilder) ->
        locationId == null
            ? null : criteriaBuilder.equal(root.get("location").get("id"), locationId);

  }

  /**
   * Specification for filtering appointments by a date range.
   */
  public static Specification<MedicalTestAppointment> hasDateTimeBetween(
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return (root, query, criteriaBuilder) -> criteriaBuilder
        .between(root.get("dateTime"), startDateTime, endDateTime);
  }

  /**
   * Specification to exclude appointments with certain statuses.
   */
  public static Specification<MedicalTestAppointment> hasStatusNotIn(
      List<AppointmentStatus> excludedStatuses) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.not(root.get("status").in(excludedStatuses));
  }

  /**
   * Specification to filter by patient.
   */
  public static Specification<MedicalTestAppointment> hasPatientId(Long patientId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("patient").get("id"), patientId);
  }
}