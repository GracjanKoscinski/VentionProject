package com.ventionteams.medfast.filter;

import com.ventionteams.medfast.service.UserService;
import com.ventionteams.medfast.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter that handles the JWT token and authenticates the user.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String BEARER_PREFIX = "Bearer ";
  public static final String HEADER_NAME = "Authorization";

  private final JwtService jwtService;
  private final UserService userService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader = request.getHeader(HEADER_NAME);
    if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = authHeader.substring(BEARER_PREFIX.length());
    if (jwtService.isTokenBlacklisted(jwt)) {
      filterChain.doFilter(request, response);
      return;
    }

    String username = jwtService.extractUserName(jwt);

    if (StringUtils.isNotEmpty(username)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(username);

      if (jwtService.isTokenValid(jwt, userDetails)) {
        if (userDetails.isCredentialsNonExpired()) {
          SecurityContext context = SecurityContextHolder.createEmptyContext();

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          context.setAuthentication(authToken);
          SecurityContextHolder.setContext(context);

        } else {
          throw new CredentialsExpiredException("Credentials have expired for user " + username);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
