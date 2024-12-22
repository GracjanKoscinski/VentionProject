package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a refresh token entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class RefreshTokenProvider implements EntityProvider<RefreshToken> {

  private final Faker faker;
  private final RefreshTokenRepository refreshTokenRepository;

  /**
   * Provides a refresh token entity.
   *
   * @param references it is list of next entities: User;
   * @return Doctor
   */
  @Override
  public RefreshToken provide(List<Object> references) {
    String customUuid = "550e8400-e29b-41d4-a716-" + faker.numerify("############");
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(customUuid);

    references.forEach(reference -> {
      if (reference instanceof User user) {
        refreshToken.setUser(user);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the OneTimePasswordEntityProvider",
            reference,
            "reference");
      }
    });

    return refreshTokenRepository.save(refreshToken);
  }
}
