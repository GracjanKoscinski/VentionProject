package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.MedicalTestResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.mapper.MedicalTestToResponseMapper;
import com.ventionteams.medfast.service.MedicalTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Medical tests controller that handles the test requests.
 */
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Medical test Controller", description = "Operations related to medical tests")
@RequestMapping("/api/patient/tests")
public class MedicalTestController {

  private final MedicalTestService medicalTestService;
  private final MedicalTestToResponseMapper mapper;

  /**
   * Provides the tests for the logged-in user.
   */
  @Operation(summary = "Request the list of available tests")
  @GetMapping
  public ResponseEntity<StandardizedResponse<List<MedicalTestResponse>>> getTestsByKeyword(
      @RequestParam(name = "keyword", required = false) Optional<String> keyword
  ) {
    List<MedicalTest> medicalTests = medicalTestService.findByKeyword(keyword);
    List<MedicalTestResponse> medicalTestResponses = mapper.apply(medicalTests);
    StandardizedResponse<List<MedicalTestResponse>> response = StandardizedResponse.ok(
        medicalTestResponses,
        HttpStatus.OK.value(),
        "Operation successful");
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
