package com.ventionteams.medfast.mapper.appointments;

import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.mapper.doctor.DoctorToDoctorSummaryResponseMapper;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of consultation appointments to a list of appointment responses.
 */
@Component
@RequiredArgsConstructor
public class AppointmentsToResponseMapper implements Function<List<ConsultationAppointment>,
    List<AppointmentResponse>> {

  private final DoctorToDoctorSummaryResponseMapper doctorToDoctorSummaryResponseMapper;

  @Override
  public List<AppointmentResponse> apply(List<ConsultationAppointment> consultationAppointments) {
    return consultationAppointments.stream()
        .map(appointment -> {
          String locationString = Optional.ofNullable(appointment.getLocation())
              .map(Location::toString)
              .orElse("");

          return AppointmentResponse.builder()
              .id(appointment.getId())
              .doctorSummary(doctorToDoctorSummaryResponseMapper.apply(appointment.getDoctor()))
              .dateFrom(appointment.getDateFrom().toString())
              .dateTo(appointment.getDateTo().toString())
              .location(locationString)
              .status(appointment.getStatus().toString())
              .type(appointment.getType().toString())
              .build();
        })
        .toList();
  }
}
