package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.LocationResponse;
import com.ventionteams.medfast.entity.Location;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of location to a list of location responses.
 */
@Component
public class LocationToResponseMapper implements Function<List<Location>, List<LocationResponse>> {

  @Override
  public List<LocationResponse> apply(List<Location> locationList) {
    return locationList.stream()
    .map(this::convertToLocationResponse)
    .collect(Collectors.toList());
  }

  /**
    * Converts a Location entity to a LocationResponse DTO.
    *
    * @param location The Location entity
    */
  private LocationResponse convertToLocationResponse(Location location) {
    return LocationResponse.builder()
          .id(location.getId())
          .hospitalName(location.getHospitalName())
          .streetAddress(location.getStreetAddress())
          .house(location.getHouse())
          .build();
  }
}
