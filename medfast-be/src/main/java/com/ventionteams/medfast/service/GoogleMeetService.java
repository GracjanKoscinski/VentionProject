package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.GoogleConfig;
import com.ventionteams.medfast.config.properties.GoogleConfig.Meet;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class that provides google meet link.
 */
@Service
@RequiredArgsConstructor
@Data
public class GoogleMeetService {

  private final GoogleConfig googleConfig;

  public Meet getGoogleMeetLink() {
    return googleConfig.meet();
  }
}