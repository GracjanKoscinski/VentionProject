package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.repository.MedicalTestRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a medical test entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class MedicalTestProvider implements EntityProvider<MedicalTest> {

  private List<String> tests = new ArrayList<>(List.of(
      "Abdominal Ultrasound",
      "Allergy Skin Test",
      "Amniocentesis",
      "Arterial Blood Gas (ABG) Test",
      "Audiometry (Hearing Test)",
      "Basic Metabolic Panel (BMP)",
      "Blood Culture",
      "Blood Glucose Test",
      "Blood Typing",
      "Blood Urea Nitrogen (BUN) Test",
      "Bone Density Scan (DEXA)",
      "Bone Marrow Biopsy",
      "Breast Ultrasound",
      "Bronchoscopy"
  ));
  private final MedicalTestRepository medicalTestRepository;
  private final Random random;
  private final Faker faker;

  /**
   * Provides a medical test entity.
   */
  @Override
  @Transactional
  public MedicalTest provide(List<Object> references) {
    MedicalTest medicalTest = new MedicalTest();
    medicalTest.setTest(generateTest());
    medicalTest.setDuration(faker.duration().atMostMinutes(90).toMinutes());
    List<Location> locations = new ArrayList<>();

    references.forEach(reference -> {
      if (reference instanceof Location location) {
        locations.add(location);
        location.getTests().add(medicalTest); 
      } else {
        throw new WrongClassException(
                "Invalid reference type passed to MedicalTestProvider",
                reference,
                "reference"
        );
      }
    });

    medicalTest.setLocations(locations);

    return medicalTestRepository.save(medicalTest);
  }

  /**
   * Provides a name for medical test.
   */
  public String generateTest() {
    int rand = random.nextInt(tests.size());
    String test = tests.get(rand);
    tests.remove(rand);
    return test;
  }
}
