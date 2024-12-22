package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.repository.HospitalServiceRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a hospitalService entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class HospitalServiceProvider implements EntityProvider<HospitalService> {

  private final Faker faker;
  private final HospitalServiceRepository hospitalServiceRepository;

  @Override
  public HospitalService provide() {

    return hospitalServiceRepository.save(
        HospitalService.builder()
            .service(faker.medicalProcedure().icd10())
            .duration(faker.duration().atMostMinutes(120).toMinutes())
            .specializations(new ArrayList<>())
            .recommendations(new ArrayList<>())
            .consultationAppointments(new ArrayList<>())
            .build());
  }
}
