package com.ventionteams.medfast.exception.appointment;

import com.ventionteams.medfast.entity.base.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the consultation appointment is completed or cancelled.
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CompletedOrMissedAppointmentException extends RuntimeException {

  private final Class<? extends BaseEntity> entityClass;
  private final Long entityId;

  /**
   * Constructor with class and id.
   */
  public <T extends BaseEntity> CompletedOrMissedAppointmentException(Class<T> entityClass,
      Long id) {
    super(String.format("%s with id %d has already been completed or missed "
        + "and cannot be modified.", entityClass.getSimpleName(), id));
    this.entityClass = entityClass;
    this.entityId = id;
  }

  @Override
  public String toString() {
    return String.format(
        "EntityNotFoundException{entityClass=%s, entityId=%s, message=%s}",
        entityClass.getSimpleName(),
        entityId,
        getMessage());
  }
}
