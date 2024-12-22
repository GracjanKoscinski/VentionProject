package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.ConsultationAppointmentType;
import com.ventionteams.medfast.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a consultation appointment entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class ConsultationAppointmentProvider implements EntityProvider<ConsultationAppointment> {

  private final AppointmentRepository appointmentRepository;
  private final Faker faker;

  /**
   * Provides a consultation appointment entity.
   *
   * @param references it is list of next entities: Doctor, Patient, Location
   * @return ConsultationAppointment
   */
  @Override
  @Transactional
  public ConsultationAppointment provide(List<Object> references) {
    LocalDateTime dateTime = LocalDate.now().plusDays(1).atTime(faker.random().nextInt(9, 17), 0);

    ConsultationAppointment consultationAppointment = ConsultationAppointment.builder()
        .dateFrom(dateTime)
        .dateTo(dateTime)
        .type(ConsultationAppointmentType.ONLINE)
        .status(AppointmentStatus.SCHEDULED)
        .build();

    references.forEach(reference -> {
      if (reference instanceof Doctor doctor) {
        consultationAppointment.setDoctor(doctor);

        List<ConsultationAppointment> consultationAppointments = doctor
            .getConsultationAppointments();
        consultationAppointments.add(consultationAppointment);
        doctor.setConsultationAppointments(consultationAppointments);
      } else if (reference instanceof Patient patient) {
        consultationAppointment.setPatient(patient);

        List<ConsultationAppointment> consultationAppointments = patient
            .getConsultationAppointments();
        consultationAppointments.add(consultationAppointment);
        patient.setConsultationAppointments(consultationAppointments);
      } else if (reference instanceof Location location) {
        consultationAppointment.setLocation(location);
        consultationAppointment.setType(ConsultationAppointmentType.ONSITE);

        List<ConsultationAppointment> consultationAppointments = location
            .getConsultationAppointments();
        consultationAppointments.add(consultationAppointment);
        location.setConsultationAppointments(consultationAppointments);
      } else if (reference instanceof AppointmentStatus status) {
        consultationAppointment.setStatus(status);
      } else if (reference instanceof HospitalService hospitalService) {
        consultationAppointment.setService(hospitalService);

        List<ConsultationAppointment> consultationAppointments = hospitalService
            .getConsultationAppointments();
        consultationAppointments.add(consultationAppointment);
        hospitalService.setConsultationAppointments(consultationAppointments);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the ConsultationAppointmentProvider",
            reference,
            "reference");
      }
    });

    return appointmentRepository.save(consultationAppointment);
  }
}
