package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.ContactInfo;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.repository.ContactInfoRepository;
import com.ventionteams.medfast.repository.PersonRepository;
import java.util.Random;
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
public class PersonProvider implements EntityProvider<Person> {

  private final PersonRepository personRepository;
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
  public Person provide() {
    ContactInfo contactInfo = ContactInfo.builder()
        .phone(faker.phoneNumber().subscriberNumber(11))
        .build();
    contactInfoRepository.save(contactInfo);

    return personRepository.save(Person.builder()
        .birthDate(faker.timeAndDate().birthday())
        .name(faker.name().firstName())
        .surname(faker.name().lastName())
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
