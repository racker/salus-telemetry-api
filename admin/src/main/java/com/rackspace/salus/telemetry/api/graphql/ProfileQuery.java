package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.Profile;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class ProfileQuery {

  @GraphQLQuery
  public Profile currentProfile() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    final Profile profile = new Profile();
    profile.setUsername(authentication.getName());
    profile.setAuthorities(
        authentication.getAuthorities().stream()
            .map(o -> ((GrantedAuthority) o).getAuthority())
            .collect(Collectors.toList())
    );

    return profile;
  }
}
