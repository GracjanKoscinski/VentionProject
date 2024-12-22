package com.ventionteams.medfast.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.filter.CustomAccessDeniedHandler;
import com.ventionteams.medfast.filter.CustomLogoutHandler;
import com.ventionteams.medfast.filter.FilterChainExceptionHandler;
import com.ventionteams.medfast.filter.JwtAuthenticationEntryPoint;
import com.ventionteams.medfast.filter.JwtAuthenticationFilter;
import com.ventionteams.medfast.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

/**
 * Security configuration for Spring Security. Configures the security filter chain and the JWT
 * authentication filter. Along with the authentication provider and the password encoder beans.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomLogoutHandler customLogoutHandler;
  private final UserService userService;
  private final FilterChainExceptionHandler filterChainExceptionHandler;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  /**
   * Configures the security filter chain for the application.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(request -> {
          var corsConfiguration = new CorsConfiguration();
          corsConfiguration.setAllowedOriginPatterns(List.of("*"));
          corsConfiguration.setAllowedMethods(
              List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
          corsConfiguration.setAllowedHeaders(List.of("*"));
          corsConfiguration.setAllowCredentials(true);
          return corsConfiguration;
        }))
        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/doctor/settings/password/setPermanentPassword")
            .permitAll()
            .requestMatchers("/api/patient/test-appointments/generate", "/api/admin-console/**")
            .hasRole(Role.ADMIN.name())
            .requestMatchers("/api/doctors/*/appointments/occupied",
                "/api/locations", "/api/services/recommendations", "/api/patient/*")
            .hasRole(Role.PATIENT.name())
            .requestMatchers("/auth/**", "/error/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**")
            .permitAll()
            .anyRequest().authenticated())
        .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(filterChainExceptionHandler, CorsFilter.class)
        .exceptionHandling(exceptionHandling -> {
          exceptionHandling.accessDeniedHandler(accessDeniedHandler);
          exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint);
        })
        .logout(logout -> logout
            .logoutUrl("/auth/logout")
            .addLogoutHandler(customLogoutHandler)
            .logoutSuccessHandler(((request, response, authentication) ->
                SecurityContextHolder.clearContext()
            ))
        );

    return http.build();
  }

  /**
   * Password encoder bean.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Authentication provider bean.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService.getUserDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Authentication manager bean.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
