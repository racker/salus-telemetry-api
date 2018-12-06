package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.api.model.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProfileQuery implements GraphQLQueryResolver {

  public Profile currentProfile() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    final Profile profile = new Profile();
    profile.setUsername(authentication.getName());

    return profile;
  }
}
