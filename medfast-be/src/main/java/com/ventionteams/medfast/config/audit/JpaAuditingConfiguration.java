package com.ventionteams.medfast.config.audit;

import com.ventionteams.medfast.entity.User;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Jpa auditor configuration class. Provides the auditor that will be written to the created_by and
 * modified_by or system if no user is authenticated and tracks the creation and modification
 * dates.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {

  /**
   * Auditor provider that provides the current user as the auditor or system if no user is logged
   * in.
   */
  @Bean
  public @NotNull AuditorAware<String> auditorProvider() {
    return () -> {
      Authentication authentication =
          SecurityContextHolder
              .getContext()
              .getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()
          || authentication instanceof AnonymousAuthenticationToken) {
        // DEVNOTE: In case no authentication was done return system as the auditor
        return Optional.of("system");
      }

      User userPrincipal = (User) authentication.getPrincipal();
      return Optional.ofNullable(userPrincipal.getUsername());
    };
  }
}
