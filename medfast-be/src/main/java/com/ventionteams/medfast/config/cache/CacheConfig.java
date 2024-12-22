package com.ventionteams.medfast.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.ventionteams.medfast.config.properties.TokenConfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration.
 */
@Configuration
@RequiredArgsConstructor
public class CacheConfig {
  private final TokenConfig tokenConfig;

  /**
   * Configures and provides a CacheManager bean for managing caches.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("blacklistedTokens");

    cacheManager.setCaffeine(Caffeine
        .newBuilder()
        .expireAfterWrite(Duration.ofSeconds(tokenConfig.timeout().access()))
    );

    return cacheManager;
  }
}
