package com.ventionteams.medfast.config.util.api;

import java.util.List;
import org.testcontainers.shaded.org.apache.commons.lang3.NotImplementedException;

/**
 * Provides an entity for further integration testing.
 *
 * @param <T> the type of entity to provide
 */
public interface EntityProvider<T> {

  default T provide() {
    throw new NotImplementedException("The provide method is not implemented.");
  }

  default T provide(List<Object> references) {
    throw new NotImplementedException("The provide with references method is not implemented");
  }
}
