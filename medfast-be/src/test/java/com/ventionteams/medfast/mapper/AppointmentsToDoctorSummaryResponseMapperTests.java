package com.ventionteams.medfast.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.mapper.appointments.AppointmentsToDoctorSummaryResponseMapper;
import com.ventionteams.medfast.mapper.doctor.DoctorToDoctorSummaryResponseMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks AppointmentsToDoctorSummary mapper functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentsToDoctorSummaryResponseMapperTests {

  @Mock
  private DoctorToDoctorSummaryResponseMapper doctorToDoctorSummaryResponseMapper;
  @InjectMocks
  private AppointmentsToDoctorSummaryResponseMapper appointmentsToDoctorSummaryResponseMapper;
  private static List<ConsultationAppointment> appointments;
  private static DoctorSummaryResponse expectedDoctorSummary;

  @BeforeAll
  static void setUp() {
    Doctor doctor = Doctor.builder()
        .id(3L)
        .specializations(List.of(
            Specialization.builder().specialization("Cardiologist").build()))
        .user(new User())
        .name("Anton")
        .surname("Dybko")
        .build();
    appointments = List.of(
        ConsultationAppointment.builder().doctor(doctor).build()
    );

    expectedDoctorSummary = DoctorSummaryResponse.builder()
        .fullName(doctor.getName() + " " + doctor.getSurname())
        .specializations(List.of("Cardiologist"))
        .id(doctor.getId())
        .build();
  }

  @Test
  public void apply_AppointmentsList_ReturnsDoctorSummaryList() {
    when(doctorToDoctorSummaryResponseMapper.apply(any(Doctor.class)))
        .thenReturn(expectedDoctorSummary);

    DoctorSummaryResponse doctorSummary =
        appointmentsToDoctorSummaryResponseMapper.apply(appointments).get(0);

    verify(doctorToDoctorSummaryResponseMapper).apply(any(Doctor.class));
    assertEquals(expectedDoctorSummary.getFullName(), doctorSummary.getFullName());
    assertEquals(expectedDoctorSummary.getId(), doctorSummary.getId());
    assertEquals(expectedDoctorSummary.getSpecializations(), doctorSummary.getSpecializations());
  }
}
