package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides user entity for further integration testing. Please use the *
 * {@link #getRawPassword(String)} method to get the raw password of the user.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class UserProvider implements EntityProvider<User> {

  private final Map<String, String> userRawPasswords = new HashMap<>();

  private final Faker faker;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Provides a user entity.
   *
   * @param references it is list of next entities: Patient, Doctor
   * @return User
   */
  @Override
  @Transactional
  public User provide(List<Object> references) {
    String password = faker
        .internet()
        .password(10, 50, true,
            true, true);

    User user = User.builder()
        .email(faker.internet().emailAddress())
        .password(passwordEncoder.encode(password))
        .enabled(true)
        .userStatus(UserStatus.ACTIVE)
        .checkboxTermsAndConditions(true)
        .build();

    references.forEach(reference -> {
      if (reference instanceof Patient patient) {
        user.setRole(Role.PATIENT);
        user.setPerson(patient);
        patient.setUser(user);
      } else if (reference instanceof Doctor doctor) {
        user.setRole(Role.DOCTOR);
        user.setPerson(doctor);
        user.setCheckboxTermsAndConditions(false);
        doctor.setUser(user);
      } else if (reference instanceof Person adminPerson) {
        user.setRole(Role.ADMIN);
        user.setPerson(adminPerson);
        user.setCheckboxTermsAndConditions(false);
        adminPerson.setUser(user);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the UserProvider",
            reference,
            "reference");
      }
    });

    User savedUser = userRepository.save(user);
    userRawPasswords.put(savedUser.getEmail(), password);
    return savedUser;
  }

  public String getRawPassword(String email) {
    return userRawPasswords.get(email);
  }

}
