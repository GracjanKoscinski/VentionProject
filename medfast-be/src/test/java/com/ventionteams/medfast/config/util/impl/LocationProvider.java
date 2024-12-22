package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.repository.LocationRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a location entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class LocationProvider implements EntityProvider<Location> {

  private final LocationRepository locationRepository;
  private final Faker faker;

  /**
   * Provides a location entity.
   *
   * @return Location
   */
  @Override
  @Transactional
  public Location provide() {
    String streetAddress = faker.address().streetAddress().chars().limit(50)
        .mapToObj(c -> String.valueOf((char) c))
        .collect(Collectors.joining());

    return locationRepository.save(Location.builder()
        .hospitalName(faker.company().name() + " Hospital")
        .streetAddress(streetAddress)
        .house(faker.address().buildingNumber())
        .consultationAppointments(new ArrayList<>())
        .tests(new ArrayList<>())
        .build());
  }
}
