package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the user verification token entity.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  Optional<VerificationToken> findByUserEmail(String email);
}
