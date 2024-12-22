package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with a time slot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a time slot")
public class TimeSlotResponse {

  @Schema(description = "Date and time from in 'yyyy-MM-ddTHH:mm:ss' format",
      example = "2021-05-03T10:30:00")
  String startTime;

  @Schema(description = "Date and time from in 'yyyy-MM-ddTHH:mm:ss' format",
      example = "2021-05-03T11:00:00")
  String endTime;
}
