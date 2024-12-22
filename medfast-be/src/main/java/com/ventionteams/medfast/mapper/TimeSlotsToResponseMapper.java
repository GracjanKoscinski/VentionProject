package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.TimeSlotResponse;
import com.ventionteams.medfast.service.TimeSlotService.TimeSlot;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of time slots to a list of time slots responses.
 */
@Component
public class TimeSlotsToResponseMapper implements Function<List<TimeSlot>, List<TimeSlotResponse>> {

  @Override
  public List<TimeSlotResponse> apply(List<TimeSlot> timeSlotList) {
    return timeSlotList.stream().map(timeSlot -> TimeSlotResponse.builder()
        .startTime(timeSlot.getStartTime().toString())
        .endTime(timeSlot.getEndTime().toString())
        .build()).toList();
  }
}
