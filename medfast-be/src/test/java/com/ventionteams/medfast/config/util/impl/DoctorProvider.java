package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.ContactInfo;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.repository.ContactInfoRepository;
import com.ventionteams.medfast.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a doctor entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class DoctorProvider implements EntityProvider<Doctor> {

  private final DoctorRepository doctorRepository;
  private final ContactInfoRepository contactInfoRepository;
  private final Faker faker;

  /**
   * Provides a doctor entity.
   *
   * @param references it is list of next entities: Location, List&lt;Specialization&gt;
   * @return Doctor
   */
  @Override
  @Transactional
  public Doctor provide(List<Object> references) {

    ContactInfo contactInfo = ContactInfo.builder()
        .phone(faker.phoneNumber().subscriberNumber(11))
        .build();
    contactInfoRepository.save(contactInfo);

    Doctor doctor = Doctor.builder()
        .licenseNumber(faker.regexify("[A-Z]{3}-[0-9]{6}"))
        .consultationAppointments(new ArrayList<>())
        .birthDate(faker.timeAndDate().birthday())
        .name(faker.name().firstName())
        .surname(faker.name().lastName())
        .sex(Gender.NEUTRAL)
        .contactInfo(contactInfo)
        .build();

    references.forEach(reference -> {
      if (reference instanceof Location location) {
        doctor.setLocation(location);
        location.setDoctors(List.of(doctor));
      } else if (reference instanceof List<?> list) {
        if (list.stream().allMatch(element -> element instanceof Specialization)) {
          List<Specialization> specializationList = list.stream()
              .map(element -> (Specialization) element)
              .toList();
          doctor.setSpecializations(specializationList);
          specializationList.forEach(specialization -> {
            List<Doctor> doctors = specialization.getDoctors();
            doctors.add(doctor);
            specialization.setDoctors(doctors);
          });
        }
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the DoctorProvider",
            reference,
            "reference");
      }
    });

    return doctorRepository.save(doctor);
  }
}
