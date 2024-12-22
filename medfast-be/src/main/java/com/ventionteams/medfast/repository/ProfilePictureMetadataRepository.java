package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.ProfilePictureMetadata;
import com.ventionteams.medfast.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the user's profile picture entity.
 */
public interface ProfilePictureMetadataRepository extends
    JpaRepository<ProfilePictureMetadata, Long> {
  Optional<ProfilePictureMetadata> findByUser(User user);

  boolean existsByUser(User user);

}
