package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with a referral.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a referral")
public class ReferralResponse {

  @Schema(description = "Referral id", example = "3")
  private Long id;

  @Schema(description = "Name and surname of a doctor who issued referral",
      example = "Anton Dybko")
  private String issuedBy;

  @Schema(description = "Specialization of a doctor whose service is needed",
      example = "Cardiac Surgeon")
  private String specialization;

  @Schema(description = "Date of issue", example = "2021-07-09")
  private LocalDate dateOfIssue;

  @Schema(description = "Expiration date", example = "2021-07-09")
  private LocalDate expirationDate;

  @Schema(description = "Expiration date", example = "Scheduled")
  private String appointmentStatus;

  @Schema(description = "Id of related appointment", example = "5")
  private Long appointmentId;
}
