package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.ReferralResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.mapper.ReferralsToResponseMapper;
import com.ventionteams.medfast.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller with endpoints related to referrals.
 */
@Log4j2
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Referral Controller", description = "Operations related to referrals")
public class ReferralController {

  private final ReferralService referralService;
  private final ReferralsToResponseMapper referralsToResponseMapper;

  /**
   * Get patient's referrals.
   */
  @Operation(summary = "Get referrals")
  @GetMapping("/patient/referrals")
  public ResponseEntity<StandardizedResponse<List<ReferralResponse>>> getReferrals(
      @AuthenticationPrincipal User authenticatedUser,
      @RequestParam(name = "elementSelection") ElementSelection selection,
      @RequestParam(name = "referralType") AppointmentRequestType referralType) {

    List<ReferralResponse> referrals = referralsToResponseMapper.apply(
        referralService.getReferrals(authenticatedUser, selection, referralType));

    StandardizedResponse<List<ReferralResponse>> response =
        StandardizedResponse.ok(
            referrals,
            HttpStatus.OK.value(),
            "Operation successful");
    log.info("Referrals successfully acquired for user with credential "
        + authenticatedUser.getEmail() + ". Status code: " + response.getStatus());

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
