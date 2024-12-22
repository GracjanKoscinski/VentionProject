package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a test appointment entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class MedicalTestAppointmentProvider implements EntityProvider<MedicalTestAppointment> {

  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  /**
   * Provides a test appointment entity.
   *
   * @param references it is list of next entities: Doctor, Patient, AppointmentRequestType
   * @return MedicalTestAppointment
   */
  @Override
  @Transactional
  public MedicalTestAppointment provide(List<Object> references) {
    MedicalTestAppointment medicalTestAppointment = MedicalTestAppointment.builder()
        .status(AppointmentStatus.SCHEDULED)
        .build();

    references.forEach(reference -> {
      if (reference instanceof MedicalTest test) {
        medicalTestAppointment.setTest(test);
      } else if (reference instanceof Patient patient) {
        medicalTestAppointment.setPatient(patient);
      } else if (reference instanceof Location location) {
        medicalTestAppointment.setLocation(location);
      } else if (reference instanceof AppointmentStatus status) {
        medicalTestAppointment.setStatus(status);
      } else if (reference instanceof AppointmentRequestType type) {
        medicalTestAppointment.setDateTime(type == AppointmentRequestType.PAST
            ? LocalDateTime.now().minusDays(1)
            : LocalDateTime.now().plusDays(1));
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the ConsultationAppointmentProvider",
            reference,
            "reference");
      }
    });
    return medicalTestAppointmentRepository.save(medicalTestAppointment);
  }
}
