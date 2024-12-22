package com.ventionteams.medfast.service.filesystem;

import com.ventionteams.medfast.exception.filesystem.FileOperationException;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Service for storing files in the file system.
 */
@Log4j2
@Service
@Data
@RequiredArgsConstructor
public class FileSystemService {

  @Value("${file.storage.location.base-directory}")
  private String location;
  private Path fileStorageLocation;
  private final ResourceLoader resourceLoader;

  /**
   * Initializing base directory to store uploads.
   */
  @PostConstruct
  public void init() {
    this.fileStorageLocation = Paths.get(location).toAbsolutePath().normalize();
    try {
      Files.createDirectories(fileStorageLocation);
    } catch (IOException e) {
      throw new FileOperationException("Could not create directory: "
          + fileStorageLocation.toAbsolutePath() + e.getMessage());
    }
  }

  /**
   * Saves uploaded file.
   */
  public void saveFile(MultipartFile file, String path) {
    try {
      file.transferTo(new File(path));
    } catch (IOException e) {
      throw new FileOperationException("Could not save file: " + e.getMessage());
    }
  }


  /**
   * Provides a file from a given path.
   */
  public byte[] getFile(String path) {
    try {
      Resource resource = path.startsWith("classpath:")
            ? resourceLoader.getResource(path)
            : resourceLoader.getResource("file:" + path);
      if (!resource.exists() || !resource.isFile()) {
        throw new FileOperationException("Could not read file: " + path);
      }
      return Files.readAllBytes(resource.getFile().toPath());
    } catch (IOException e) {
      throw new FileOperationException("Could not read file: " + e.getMessage());
    }
  }

  /**
   * Deletes a file in a given path.
   */
  public void deleteFile(String path) {
    try {
      Files.deleteIfExists(new File(path).toPath());
    } catch (IOException e) {
      throw new FileOperationException("Could not delete file: " + e.getMessage());
    }
  }
}