package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the refresh token entity.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);
}
