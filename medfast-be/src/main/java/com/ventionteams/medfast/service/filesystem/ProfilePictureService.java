package com.ventionteams.medfast.service.filesystem;

import com.ventionteams.medfast.config.properties.ProfileConfig;
import com.ventionteams.medfast.dto.response.userinfo.UserPhotoResponse;
import com.ventionteams.medfast.entity.ProfilePictureMetadata;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.exception.filesystem.FileOperationException;
import com.ventionteams.medfast.exception.userdetails.InvalidExtensionException;
import com.ventionteams.medfast.repository.ProfilePictureMetadataRepository;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for operations on user's profile pictures.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ProfilePictureService {

  private final ProfilePictureMetadataRepository profilePictureMetadataRepository;
  private final FileSystemService fileSystemService;
  private final ProfileConfig profileConfig;
  private static final String PROFILE_PICTURES_DIRECTORY = "profilePictures";
  private Path profilePicturesLocation;

  /**
   * Initializing  subdirectory to store profile pictures.
   */
  @PostConstruct
  public void init() {
    this.profilePicturesLocation = fileSystemService
        .getFileStorageLocation().resolve(PROFILE_PICTURES_DIRECTORY);
    try {
      Files.createDirectories(profilePicturesLocation);
    } catch (IOException e) {
      throw new FileOperationException("Could not create directory: "
          + profilePicturesLocation.toAbsolutePath() + e.getMessage());
    }
  }

  /**
   * Saves user's profile picture.
   */
  @Transactional(rollbackFor = IOException.class)
  public void saveProfilePicture(User user, MultipartFile file) {

    if (!validateProfilePictureExtension(Objects.requireNonNull(file.getContentType()))) {
      throw new InvalidExtensionException();
    }
    if (profilePictureMetadataRepository.existsByUser(user)) {
      updateProfilePicture(user, file);
    } else {
      String path = createPathToFile(user, file.getContentType()).toString();
      ProfilePictureMetadata profilePictureMetadata = ProfilePictureMetadata.builder()
          .filePath(path)
          .contentType(file.getContentType())
          .user(user)
          .build();
      log.info("Attempt to save photo for the user with id {}", user.getId());
      profilePictureMetadataRepository.save(profilePictureMetadata);
      fileSystemService.saveFile(file, path);
    }
  }

  /**
   * Saves user's profile picture.
   */
  @Transactional(rollbackFor = IOException.class)
  public void updateProfilePicture(User user, MultipartFile file) {
    ProfilePictureMetadata profilePictureMetadata =
        profilePictureMetadataRepository.findByUser(user).orElseThrow(
            () -> new EntityNotFoundException(ProfilePictureMetadata.class,
                user.getEmail()));
    String path = createPathToFile(user, Objects.requireNonNull(file.getContentType())).toString();
    profilePictureMetadata.setFilePath(path);
    profilePictureMetadata.setContentType(file.getContentType());
    log.info("Attempt to update photo for the user with id {}", user.getId());
    profilePictureMetadataRepository.save(profilePictureMetadata);
    fileSystemService.saveFile(file, path);
  }

  /**
   * Provides a profile picture for a given user.
   */
  public UserPhotoResponse getProfilePicture(User user) {
    ProfilePictureMetadata profilePictureMetadataEntity =
        profilePictureMetadataRepository.findByUser(user)
            .orElse(ProfilePictureMetadata.builder()
                .filePath(profileConfig.defaultProfilePicturePath())
                .contentType("image/png")
                .build());
    String path = profilePictureMetadataEntity.getFilePath();
    byte[] userPhoto = isFileAccessible(path)
        ? fileSystemService.getFile(path)
        : fileSystemService.getFile(profileConfig.defaultProfilePicturePath());
    return UserPhotoResponse.builder()
        .userPhoto(userPhoto)
        .contentType(profilePictureMetadataEntity.getContentType())
        .build();
  }

  /**
    * Checks if the file at the specified path exists and is readable.
    */
  private boolean isFileAccessible(String path) {
    File file = new File(path);
    return file.exists() && file.canRead();
  }

  /**
   * Deletes a profile picture for a given user.
   */
  @Transactional(rollbackFor = IOException.class)
  public void deleteProfilePicture(User user) {
    ProfilePictureMetadata pfp = profilePictureMetadataRepository.findByUser(user).orElseThrow(
        () -> new EntityNotFoundException(ProfilePictureMetadata.class,
            user.getEmail()));
    String path = pfp.getFilePath();
    log.info("Attempt to delete photo for the user with id {}", user.getId());
    profilePictureMetadataRepository.delete(pfp);
    fileSystemService.deleteFile(path);
  }

  private boolean validateProfilePictureExtension(String contentType) {
    String mimeType = contentType.split(";")[0].trim();
    return mimeType.equals("image/jpeg")
        || mimeType.equals("image/png")
        || mimeType.equals("image/gif")
        || mimeType.equals("image/jpg");
  }

  /**
   * Creates a path to the file based on user id and provided extension.
   */
  public Path createPathToFile(User user, String contentType) {
    Long id = user.getId();
    String mimeType = contentType.split(";")[0].trim();
    String extension = mimeType.split("/")[1].trim();
    String fileName = id + "." + extension;
    return profilePicturesLocation.resolve(fileName);
  }
}
