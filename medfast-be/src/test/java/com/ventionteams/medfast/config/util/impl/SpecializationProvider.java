package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a specialization entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class SpecializationProvider implements EntityProvider<Specialization> {

  private List<String> specializations = new ArrayList<>(List.of(
      "Cardiologist",
      "Dermatologist",
      "Neurologist",
      "Orthopedic Surgeon",
      "Pediatrician",
      "Psychiatrist",
      "Oncologist",
      "Endocrinologist",
      "Gastroenterologist",
      "Hematologist",
      "Nephrologist",
      "Ophthalmologist",
      "Pulmonologist",
      "Rheumatologist",
      "Urologist",
      "Radiologist",
      "Anesthesiologist",
      "Pathologist",
      "Otolaryngologies (ENT)",
      "Obstetrician",
      "Gynecologist",
      "General Surgeon",
      "Plastic Surgeon",
      "Immunologist",
      "Infectious Disease Specialist",
      "Geriatrician",
      "Sports Medicine Specialist",
      "Allergist",
      "Family Medicine Physician",
      "Emergency Medicine Specialist"
  ));

  private final SpecializationRepository specializationRepository;
  private final Random random;

  /**
   * Provides a specialization entity.
   *
   * @return Specialization
   */
  @Override
  @Transactional
  public Specialization provide(List<Object> references) {
    Specialization specialization = Specialization.builder()
        .specialization(generateSpecialization())
        .doctors(new ArrayList<>())
        .build();

    references.forEach(reference -> {
      if (reference instanceof HospitalService hospitalService) {
        specialization.setServices(List.of(hospitalService));

        List<Specialization> specializations = hospitalService
            .getSpecializations();
        specializations.add(specialization);
        hospitalService.setSpecializations(specializations);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the SpecializationProvider",
            reference,
            "reference");
      }
    });

    return specializationRepository.save(specialization);
  }

  /**
   * Provides a name for medical test.
   */
  public String generateSpecialization() {
    int rand = random.nextInt(specializations.size());
    String s = specializations.get(rand);
    specializations.remove(rand);
    return s;
  }
}
