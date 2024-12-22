package com.ventionteams.medfast.mapper;

import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.dto.response.doctor.DoctorSummaryResponse;
import com.ventionteams.medfast.dto.response.userinfo.UserPhotoResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.ConsultationAppointmentType;
import com.ventionteams.medfast.mapper.appointments.AppointmentsToResponseMapper;
import com.ventionteams.medfast.mapper.doctor.DoctorToDoctorSummaryResponseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks appointments mapper functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentsToResponseMapperTests {

  HospitalService service;
  Doctor doctor;
  Specialization specialization;
  Location location;
  List<ConsultationAppointment> consultationAppointmentList;
  List<AppointmentResponse> appointmentResponseList;
  private DoctorSummaryResponse doctorSummaryResponse;

  @Mock
  private DoctorToDoctorSummaryResponseMapper doctorToDoctorSummaryResponseMapper;

  @InjectMocks
  private AppointmentsToResponseMapper appointmentsToResponseMapper;

  @BeforeEach
  void setUp() {
    service = HospitalService.builder().id(1L).service("Echocardiography").build();

    doctor = Doctor.builder().id(1L).name("John").surname("Doe")
        .specializations(List.of(Specialization.builder().specialization("Cardiology").build()))
        .build();

    specialization = new Specialization();
    specialization.setSpecialization("Cardiology");

    doctor.setSpecializations(List.of(specialization));

    location = Location.builder().hospitalName("General Hospital").house("123")
        .streetAddress("Main Street").build();

    consultationAppointmentList = List.of(
        ConsultationAppointment.builder()
            .id(1L)
            .doctor(doctor)
            .patient(null)
            .service(service)
            .dateFrom(LocalDateTime.of(2023, 8, 10, 10, 0))
            .dateTo(LocalDateTime.of(2023, 8, 10, 11, 0))
            .type(ConsultationAppointmentType.ONSITE)
            .status(AppointmentStatus.IN_CONSULTATION)
            .build()
    );

    doctorSummaryResponse = DoctorSummaryResponse.builder()
        .id(doctor.getId())
        .fullName(doctor.getName() + " " + doctor.getSurname())
        .specializations(
            doctor.getSpecializations().stream().map(Specialization::getSpecialization).toList())
        .userPhotoResponse(new UserPhotoResponse())
        .build();

    appointmentResponseList = List.of(
        AppointmentResponse.builder()
            .id(1L)
            .doctorSummary(doctorSummaryResponse)
            .dateFrom("2023-08-10T10:00")
            .dateTo("2023-08-10T11:00")
            .status("In-Consultation")
            .type(ConsultationAppointmentType.ONSITE.toString())
            .build()
    );


  }

  @Test
  public void apply_FullInformation_ReturnFullInformationResponse() {
    consultationAppointmentList.get(0).setLocation(location);
    appointmentResponseList.get(0).setLocation("General Hospital, 123 Main Street");
    when(doctorToDoctorSummaryResponseMapper.apply(doctor)).thenReturn(doctorSummaryResponse);

    List<AppointmentResponse> appointmentResponses = appointmentsToResponseMapper.apply(
        consultationAppointmentList);

    Assertions.assertThat(appointmentResponses).isEqualTo(appointmentResponseList);
  }

  @Test
  public void apply_MissingLocation_ReturnResponseWithoutLocation() {
    appointmentResponseList.get(0).setLocation("");
    when(doctorToDoctorSummaryResponseMapper.apply(doctor)).thenReturn(doctorSummaryResponse);

    List<AppointmentResponse> appointmentResponses = appointmentsToResponseMapper.apply(
        consultationAppointmentList);

    Assertions.assertThat(appointmentResponses).isEqualTo(appointmentResponseList);
  }
}
