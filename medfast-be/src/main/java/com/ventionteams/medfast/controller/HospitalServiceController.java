package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.HospitalServiceResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.mapper.HospitalServiceToResponseMapper;
import com.ventionteams.medfast.service.HospitalServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Doctor controller that handles the doctor requests.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
@Tag(name = "Service Controller", description = "Operations related to services")
public class HospitalServiceController {

  private final HospitalServiceService hospitalServiceService;
  private final HospitalServiceToResponseMapper hospitalServiceToResponseMapper;

  /**
   * Provides recommendations for the logged in patient.
   */
  @Operation(summary = "Request the list of recommended services")
  @GetMapping("/recommendations")
  public ResponseEntity<StandardizedResponse<List<HospitalServiceResponse>>> getRecommendedServices(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "elementSelection", required = false)
      ElementSelection selection) {

    StandardizedResponse<List<HospitalServiceResponse>> response;

    List<HospitalServiceResponse> recommendations = hospitalServiceToResponseMapper.apply(
        hospitalServiceService.getRecommendedServices(authenticatedUser, selection));

    response = StandardizedResponse.ok(
        recommendations,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides a list of all services.
   */
  @Operation(summary = "Request a list of all services")
  @GetMapping("")
  public ResponseEntity<StandardizedResponse<List<HospitalServiceResponse>>> getAllServices() {

    StandardizedResponse<List<HospitalServiceResponse>> response;

    List<HospitalServiceResponse> services = hospitalServiceToResponseMapper
        .apply(hospitalServiceService.getAllServices());

    response = StandardizedResponse.ok(
        services,
        HttpStatus.OK.value(),
        "Operation successful");

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
