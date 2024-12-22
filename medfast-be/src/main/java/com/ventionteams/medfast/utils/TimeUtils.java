package com.ventionteams.medfast.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Time utils.
 */
public final class TimeUtils {

  /**
   * Method which accepts time in long format and returns as string "Xh Ymin".
   */
  public static String formatDuration(long totalMinutes) {
    Duration duration = Duration.ofMinutes(totalMinutes);

    long hours = duration.toHours();
    long minutes = duration.toMinutesPart();

    // Building the result string
    StringBuilder result = new StringBuilder();
    if (hours > 0) {
      result.append(hours).append("h");
    }
    if (minutes > 0) {
      if (hours > 0) {
        result.append(" ");
      }
      result.append(minutes).append("min");
    }

    return result.toString();
  }

  /**
   * Formats the provided LocalDateTime using the format from environment variables.
   */
  public static String formatLocatDateTime(LocalDateTime dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");
    return dateTime.format(formatter);
  }
}
