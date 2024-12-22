package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO class which contains both pdf and it's name.
 */
@Data
@AllArgsConstructor
@Builder
@Schema(description = "DTO class which contains both pdf and it's name.")
public class PdfResultResponse {
  @Schema(description = "Binary data of the PDF file.")
  private final byte[] data;
  @Schema(description = "file name", example = "Anton_Dybko_2024-07-31.pdf")
  private final String name;
}
