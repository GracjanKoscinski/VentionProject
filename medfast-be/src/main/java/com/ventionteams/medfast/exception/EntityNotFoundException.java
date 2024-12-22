package com.ventionteams.medfast.exception;

import com.ventionteams.medfast.entity.base.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when entity is not found.
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

  private final Class<? extends BaseEntity> entityClass;
  private final Long entityId;
  private final String credential;

  /**
   * Constructor with class and id.
   */
  public <T extends BaseEntity> EntityNotFoundException(Class<T> entityClass, Long id) {
    super(String.format("%s with id %d not found", entityClass.getSimpleName(), id));
    this.entityClass = entityClass;
    this.entityId = id;
    this.credential = null;
  }

  /**
   * Constructor with class and user credential.
   */
  public <T extends BaseEntity> EntityNotFoundException(Class<T> entityClass, String credential) {
    super(String.format("%s was not found for user with credential %s",
        entityClass.getSimpleName(), credential));
    this.entityClass = entityClass;
    this.entityId = null;
    this.credential = credential;
  }

  @Override
  public String toString() {
    return String.format(
        "EntityNotFoundException{entityClass=%s, entityId=%s, credential=%s, message=%s}",
        entityClass.getSimpleName(),
        entityId != null ? entityId : "N/A",
        credential != null ? credential : "N/A",
        getMessage());
  }
}
