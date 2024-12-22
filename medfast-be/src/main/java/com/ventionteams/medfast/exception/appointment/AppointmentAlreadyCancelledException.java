package com.ventionteams.medfast.exception.appointment;

import com.ventionteams.medfast.entity.base.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the consultation appointment is already cancelled.
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppointmentAlreadyCancelledException extends RuntimeException {

  private final Class<? extends BaseEntity> entityClass;
  private final Long entityId;

  /**
   * Constructor with class and id.
   */
  public <T extends BaseEntity> AppointmentAlreadyCancelledException(Class<T> entityClass,
      Long id) {
    super(String.format("%s with id %d is already cancelled", entityClass.getSimpleName(), id));
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
