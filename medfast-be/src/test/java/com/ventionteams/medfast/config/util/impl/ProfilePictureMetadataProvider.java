package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.ProfilePictureMetadata;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.ProfilePictureMetadataRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a ProfilePictureMetadata entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class ProfilePictureMetadataProvider implements EntityProvider<ProfilePictureMetadata> {

  private final ProfilePictureMetadataRepository repository;

  /**
   * Provides a ProfilePictureMetadata entity.
   */
  @Override
  @Transactional
  public ProfilePictureMetadata provide(List<Object> references) {

    ProfilePictureMetadata pfp = ProfilePictureMetadata.builder()
        .filePath("src/main/resources/templates/logos/logo.png")
        .contentType("image/jpg")
        .build();

    references.forEach(reference -> {
      if (reference instanceof User user) {
        pfp.setUser(user);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the DoctorProvider",
            reference,
            "reference");
      }
    });
    return repository.save(pfp);
  }
}
