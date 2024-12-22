package com.ventionteams.medfast.mapper.appointments;

import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.mapper.doctor.DoctorToDoctorSummaryResponseMapper;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of consultation appointments to a list of doctor summaries.
 */
@Component
@RequiredArgsConstructor
public class AppointmentsToDoctorSummaryResponseMapper 
    implements Function<List<ConsultationAppointment>,
    List<DoctorSummaryResponse>> {

  private final DoctorToDoctorSummaryResponseMapper doctorToDoctorSummaryResponseMapper;

  @Override
  public List<DoctorSummaryResponse> apply(List<ConsultationAppointment> appointments) {
    return appointments.stream().map(c -> {
      Doctor doctor = c.getDoctor();
      return doctorToDoctorSummaryResponseMapper.apply(doctor);
    }).toList();
  }
}
