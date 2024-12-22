package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.Address;
import com.ventionteams.medfast.entity.ContactInfo;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.repository.AddressRepository;
import com.ventionteams.medfast.repository.ContactInfoRepository;
import com.ventionteams.medfast.repository.PatientRepository;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides a patient entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class PatientProvider implements EntityProvider<Patient> {

  private final PatientRepository patientRepository;
  private final AddressRepository addressRepository;
  private final ContactInfoRepository contactInfoRepository;
  private final Faker faker;
  private final Random random;

  /**
   * Provides a Patient entity.
   *
   * @return Patient
   */
  @Override
  @Transactional
  public Patient provide() {
    String streetAddress = faker.address().streetAddress().chars().limit(50)
        .mapToObj(c -> String.valueOf((char) c))
        .collect(Collectors.joining());

    Address address = Address.builder()
        .streetAddress(streetAddress)
        .house(faker.address().streetAddressNumber())
        .apartment(faker.address().buildingNumber())
        .city(faker.address().city())
        .state(faker.address().city())
        .zip(faker.address().zipCode())
        .build();
    addressRepository.save(address);

    ContactInfo contactInfo = ContactInfo.builder()
        .phone(faker.phoneNumber().subscriberNumber(11))
        .build();
    contactInfoRepository.save(contactInfo);

    return patientRepository.save(Patient.builder()
        .consultationAppointments(new ArrayList<>())
        .birthDate(faker.timeAndDate().birthday())
        .name(faker.name().firstName())
        .surname(faker.name().lastName())
        .address(address)
        .contactInfo(contactInfo)
        .sex(getRandomGender())
        .citizenship(faker.country().name())
        .build());
  }

  private Gender getRandomGender() {
    Gender[] genders = Gender.values();
    return genders[random.nextInt(genders.length)];
  }
}
