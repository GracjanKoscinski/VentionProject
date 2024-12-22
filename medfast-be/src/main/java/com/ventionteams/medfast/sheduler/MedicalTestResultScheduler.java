package com.ventionteams.medfast.sheduler;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.appointment.InvalidAppointmentTimeException;
import com.ventionteams.medfast.exception.medicaltestappointment.PdfForMedicalTestAlreadyExistsException;
import com.ventionteams.medfast.service.appointment.MedicalTestAppointmentService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for generating medical test results.
 */
@Component
@RequiredArgsConstructor
public class MedicalTestResultScheduler {

  private final MedicalTestAppointmentService medicalTestAppointmentService;

  /**
   * A scheduler that runs every day at a set time.
   */

  @Scheduled(cron = "0 15 08 * * ?", zone = "GMT+2")
  public void generateTestsForAllAppointments() {
    LocalDateTime startOfYesterday = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
    LocalDateTime endOfYesterday = startOfYesterday.plusDays(1);

    List<MedicalTestAppointment> appointments = medicalTestAppointmentService
        .findTestAppointmentsByDate(startOfYesterday, endOfYesterday);

    for (MedicalTestAppointment appointment : appointments) {
      try {
        medicalTestAppointmentService
            .generateTestResultForAppointment(appointment.getId());
      } catch (EntityNotFoundException
               | InvalidAppointmentTimeException
               | PdfForMedicalTestAlreadyExistsException ignoredException) {
        continue;
      }
    }
  }
}
