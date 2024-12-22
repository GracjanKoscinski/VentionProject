package com.ventionteams.medfast.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A filter that handles exceptions thrown during request processing.
 */
@Log4j2
@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException {

    try {
      filterChain.doFilter(request, response);
    } catch (JwtException ex) {

      handleException(
          response,
          ex,
          HttpStatus.UNAUTHORIZED,
          "There seems to be an issue with your login session. "
              + "It looks like the system encountered an unexpected error "
              + "while processing your request. Please try logging in again. "
              + "If the problem persists, contact support for assistance."
      );
    } catch (UsernameNotFoundException ex) {

      handleException(
          response,
          ex,
          HttpStatus.NOT_FOUND,
          "Authentication failed: User not found. "
              + "The provided credentials do not match any existing user accounts in the system."
      );
    } catch (AccessDeniedException ex) {

      handleException(
          response,
          ex,
          HttpStatus.FORBIDDEN,
          "You do not have permission to access this resource. "
              + "Please contact support if you believe this is an error."
      );
    } catch (CredentialsExpiredException ex) {

      handleException(
          response,
          ex,
          HttpStatus.UNAUTHORIZED,
          "The credentials you are using have expired. "
              + "Please set permanent password via link from mail to continue."
      );
    } catch (InsufficientAuthenticationException ex) {

      handleException(
          response,
          ex,
          HttpStatus.UNAUTHORIZED,
          "Authentication is required to access this resource. Please log in and try again."
      );
    } catch (Exception ex) {

      handleException(
          response,
          ex,
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Something went wrong on our end. We're working to fix the issue. "
              + "Please try again later, and if the problem persists, contact support for help."
      );
    }
  }

  private void handleException(
      HttpServletResponse response,
      Exception ex,
      HttpStatus status,
      String errorMessage) throws IOException {

    log.error("{} occurred in the filter chain,"
            + " details: {}",
        ex.getClass().getName(), ex.getMessage());

    response.setStatus(status.value());
    response.setContentType("application/json");
    response.getWriter().write(
        objectMapper.writeValueAsString(
            StandardizedResponse.error(
                status.value(),
                null,
                ex.getClass().getName(),
                errorMessage
            )
        )
    );
  }
}
