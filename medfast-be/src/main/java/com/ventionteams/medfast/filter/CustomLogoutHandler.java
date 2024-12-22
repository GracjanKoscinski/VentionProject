package com.ventionteams.medfast.filter;

import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.InvalidAuthorizationTokenException;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import com.ventionteams.medfast.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom logout handler for managing user logouts by invalidating JWT tokens and removing
 * associated refresh tokens.
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) {

    String authHeader = request.getHeader("Authorization");
    if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
      throw new InvalidAuthorizationTokenException();
    }
    String jwt = authHeader.substring(7);

    String email = jwtService.extractUserName(jwt);

    User currentUser = userRepository.findByEmail(email).orElseThrow(() ->
        new UsernameNotFoundException("User with credential " + email + " not found."));
    jwtService.blacklistToken(jwt);
    refreshTokenRepository.deleteByUser(currentUser);
  }
}
