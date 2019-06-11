package com.rackspace.salus.telemetry.api.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public String currentTenantId() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      return authentication.getName();
    }
    else {
      throw new IllegalStateException("Operation requires authentication");
    }
  }
}
