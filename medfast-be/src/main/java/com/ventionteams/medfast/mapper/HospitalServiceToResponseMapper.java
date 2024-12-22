package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.HospitalServiceResponse;
import com.ventionteams.medfast.entity.HospitalService;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of hospital services to a list of HospitalServiceResponses.
 */
@Component
public class HospitalServiceToResponseMapper implements Function<List<HospitalService>,
    List<HospitalServiceResponse>> {

  @Override
  public List<HospitalServiceResponse> apply(List<HospitalService> services) {
    return services.stream().map(s -> HospitalServiceResponse.builder()
        .id(s.getId())
        .service(s.getService())
        .duration(s.getDuration())
        .build()).toList();
  }
}
