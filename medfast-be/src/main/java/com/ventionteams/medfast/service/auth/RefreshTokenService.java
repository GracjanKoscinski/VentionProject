package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling refresh token operations.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;
  private final TokenConfig tokenConfig;

  /**
   * Generate a refresh token for the user.
   */
  @Transactional
  public RefreshToken generateToken(UserDetails userDetails) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userRepository.findByEmail(
        userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found")
    ));

    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  /**
   * Refresh the access token using the refresh token.
   */
  public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
        .orElseThrow(() -> new NoSuchElementException("Refresh token is not found"));

    verifyExpiration(refreshToken);
    return new JwtAuthenticationResponse(
        jwtService.generateToken(refreshToken.getUser()),
        requestRefreshToken,
        tokenConfig.timeout().access(),
        Duration.between(LocalDateTime.now(),
            refreshToken.getCreatedDate().plusSeconds(tokenConfig.timeout().refresh())).getSeconds()
    );
  }

  private void verifyExpiration(RefreshToken token) {
    long actualValidityPeriod = Duration.between(token.getCreatedDate(), LocalDateTime.now())
        .getSeconds();
    if (actualValidityPeriod > tokenConfig.timeout().refresh()) {
      refreshTokenRepository.delete(token);
      throw new TokenExpiredException(token.getToken());
    }
  }
}
