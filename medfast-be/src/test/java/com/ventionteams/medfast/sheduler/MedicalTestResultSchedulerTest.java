package com.ventionteams.medfast.sheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.appointment.MedicalTestAppointmentService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for MedicalTestScheduler.
 */
@ExtendWith(MockitoExtension.class)
public class MedicalTestResultSchedulerTest {

  @Mock
  private MedicalTestAppointmentService medicalTestAppointmentService;

  @InjectMocks
  private MedicalTestResultScheduler medicalTestResultScheduler;

  @Test
  void generateTestsForAllAppointments() {
    List<MedicalTestAppointment> appointments = getMedicalTestAppointments();

    when(medicalTestAppointmentService
        .findTestAppointmentsByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(appointments);

    doNothing().when(medicalTestAppointmentService)
        .generateTestResultForAppointment(anyLong());

    medicalTestResultScheduler.generateTestsForAllAppointments();

    verify(medicalTestAppointmentService).findTestAppointmentsByDate(
        any(LocalDateTime.class), any(LocalDateTime.class));

    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

    verify(medicalTestAppointmentService)
        .generateTestResultForAppointment(idCaptor.capture());

    assertEquals(1L, idCaptor.getValue());
  }

  private static List<MedicalTestAppointment> getMedicalTestAppointments() {
    User patientUser = new User();
    patientUser.setId(1L);

    User doctorUser = new User();
    doctorUser.setId(2L);

    Patient patient = new Patient();
    patient.setId(1L);
    patient.setUser(patientUser);

    Doctor doctor = new Doctor();
    doctor.setId(2L);
    doctor.setUser(doctorUser);

    MedicalTestAppointment appointment = new MedicalTestAppointment();
    appointment.setId(1L);
    appointment.setPatient(patient);

    return Collections.singletonList(appointment);
  }
}