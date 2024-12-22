package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a one time password entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class OneTimePasswordEntityProvider implements EntityProvider<OneTimePassword> {

  private final Faker faker;
  private final OneTimePasswordRepository oneTimePasswordRepository;

  /**
   * Provides a one time password entity.
   *
   * @param references it is list of next entities: User;
   * @return Doctor
   */
  @Override
  public OneTimePassword provide(List<Object> references) {
    OneTimePassword oneTimePassword = new OneTimePassword();
    oneTimePassword.setToken(faker.number().digits(4));

    references.forEach(reference -> {
      if (reference instanceof User user) {
        oneTimePassword.setUser(user);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the OneTimePasswordEntityProvider",
            reference,
            "reference");
      }
    });

    return oneTimePasswordRepository.save(oneTimePassword);
  }
}
