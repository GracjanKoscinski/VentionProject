package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.LocationResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.mapper.LocationToResponseMapper;
import com.ventionteams.medfast.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Location controller that handles the location requests.
 */
@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
@Tag(name = "Location Controller", description = "Operations related to locations")
public class LocationController {
  private final LocationService locationService;
  private final LocationToResponseMapper locationToResponseMapper;

  /**
    * Endpoint to get a list of all available locations.
    */
  @GetMapping("")
  public ResponseEntity<StandardizedResponse<List<LocationResponse>>> getAvailableLocations(
      @RequestParam("serviceId") Long serviceId,
      @RequestParam("doctorId") Long doctorId,
      @RequestParam(value = "dateTime", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime
  ) {
    List<Location> locations = 
        locationService.getAvailableLocations(serviceId, doctorId, dateTime);
    List<LocationResponse> locationResponse = locationToResponseMapper.apply(locations);

    StandardizedResponse<List<LocationResponse>> response = StandardizedResponse.ok(
        locationResponse,
        HttpStatus.OK.value(),
        "Operation successful");
    
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}