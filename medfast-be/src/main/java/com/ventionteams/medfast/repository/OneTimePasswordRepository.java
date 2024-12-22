package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.OneTimePassword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the one-time password entity.
 */
public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {

  Optional<OneTimePassword> findByUserEmailAndToken(String user, String otp);

  void deleteByUserEmailAndToken(String user, String otp);
}
