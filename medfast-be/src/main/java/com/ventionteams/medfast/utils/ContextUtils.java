package com.ventionteams.medfast.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utils to work with context.
 */
public class ContextUtils {

  /**
   * Method which extracts email from context.
   */
  public static String getEmailFromContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      String email = requestAttributes.getRequest().getParameter("email");
      if (email == null) {
        email = (String) requestAttributes.getAttribute("email", RequestAttributes.SCOPE_REQUEST);
      }
      return email;
    }

    return null;
  }
}
