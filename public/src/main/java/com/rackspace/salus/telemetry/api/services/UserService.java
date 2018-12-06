package com.rackspace.salus.telemetry.api.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public String currentTenantId() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
