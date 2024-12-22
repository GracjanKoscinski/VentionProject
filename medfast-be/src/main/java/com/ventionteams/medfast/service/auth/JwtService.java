package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * JWT service responsible for generating and validating JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final TokenConfig tokenConfig;
  private final CacheManager cacheManager;

  @CachePut(value = "blacklistedTokens", key = "#token", unless = "#result == null")
  public String blacklistToken(String token) {
    return token;
  }

  /**
   * Checks if token is present in the cache.
   */
  public boolean isTokenBlacklisted(String token) {
    Cache cache = cacheManager.getCache("blacklistedTokens");
    if (cache == null) {
      return false;
    }
    return cache.get(token) != null;
  }

  /**
   * Generate a JWT token for the user based on the user data.
   */
  public String generateToken(User userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", userDetails.getId());
    claims.put("email", userDetails.getEmail());
    claims.put("role", userDetails.getRole());
    return generateToken(claims, userDetails);
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusSeconds(tokenConfig.timeout().access())))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * checks if token is valid or no.
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    if (isTokenExpired(token)) {
      throw new ExpiredJwtException(
          null, null, "JWT token has expired for user with credential "
          + userDetails.getUsername());
    }
    return (userName.equals(userDetails.getUsername()));
  }

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(tokenConfig.signing().key());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
