package com.ventionteams.medfast.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.UserService;
import com.ventionteams.medfast.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Checks jwt authentication filter functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {

  private static final String HEADER_NAME = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Mock
  private JwtService jwtService;

  @Mock
  private UserService userService;

  @Mock
  private FilterChain filterChain;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UserDetails userDetails;

  @Mock
  private HttpServletResponse response;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  public void setup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void doFilterInternal_NoAuthHeader_FilterSkipRequest()
      throws ServletException, IOException {
    when(request.getHeader(HEADER_NAME)).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, never()).extractUserName(anyString());
  }

  @Test
  public void doFilterInternal_InvalidAuthHeader_FilterSkipRequest()
      throws ServletException, IOException {
    when(request.getHeader(HEADER_NAME)).thenReturn("InvalidHeader");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, never()).extractUserName(anyString());
  }

  @Test
  public void doFilterInternal_InvalidJwt_FilterSkipRequest() throws ServletException, IOException {
    when(request.getHeader(HEADER_NAME)).thenReturn(BEARER_PREFIX + "invalidJwt");
    when(jwtService.extractUserName(anyString())).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void doFilterInternal_InvalidJwtForUser_FilterSkipRequest()
      throws ServletException, IOException {
    String username = "user@example.com";
    UserDetails userDetails = mock(User.class);
    UserDetailsService userDetailsService = mock(UserDetailsService.class);

    when(request.getHeader(HEADER_NAME)).thenReturn(BEARER_PREFIX + "validJwt");
    when(jwtService.extractUserName(anyString())).thenReturn(username);
    when(userService.getUserDetailsService()).thenReturn(userDetailsService);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.isTokenValid("validJwt", userDetails)).thenReturn(false);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);

  }

  @Test
  public void doFilterInternal_UserAuthenticated_NoReAuthenticate()
      throws ServletException, IOException {
    SecurityContextHolder.getContext()
        .setAuthentication(mock(UsernamePasswordAuthenticationToken.class));

    when(request.getHeader(HEADER_NAME)).thenReturn(BEARER_PREFIX + "validJwt");
    when(jwtService.extractUserName(anyString())).thenReturn("user@example.com");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void doFilterInternal_CredentialsExpired_ExceptionThrown()
      throws ServletException, IOException {
    String username = "user@example.com";

    when(request.getHeader(HEADER_NAME)).thenReturn(BEARER_PREFIX + "validJwt");
    when(jwtService.extractUserName(anyString())).thenReturn(username);
    when(userService.getUserDetailsService()).thenReturn(userDetailsService);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.isTokenValid("validJwt", userDetails)).thenReturn(true);
    when(userDetails.isCredentialsNonExpired()).thenReturn(false);

    org.junit.jupiter.api.Assertions.assertThrows(CredentialsExpiredException.class,
        () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
  }

  @Test
  public void doFilterInternal_ValidJwt_FilterAuthenticatesUser()
      throws ServletException, IOException {
    String username = "user@example.com";

    when(request.getHeader(HEADER_NAME)).thenReturn(BEARER_PREFIX + "validJwt");
    when(jwtService.extractUserName(anyString())).thenReturn(username);
    when(userService.getUserDetailsService()).thenReturn(userDetailsService);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtService.isTokenValid("validJwt", userDetails)).thenReturn(true);
    when(userDetails.isCredentialsNonExpired()).thenReturn(true);
    when(userDetails.getAuthorities()).thenReturn(List.of());

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(userDetails);
  }

  @Test
  public void doFilterInternal_RequestIsNull_ExceptionThrown() {
    org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
        () -> jwtAuthenticationFilter.doFilterInternal(null, response, filterChain));
  }

  @Test
  public void doFilterInternal_ResponseIsNull_ExceptionThrown() {
    org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
        () -> jwtAuthenticationFilter.doFilterInternal(request, null, filterChain));
  }

  @Test
  public void doFilterInternal_FilterChainIsNull_ExceptionThrown() {
    org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
        () -> jwtAuthenticationFilter.doFilterInternal(request, response, null));
  }

}
