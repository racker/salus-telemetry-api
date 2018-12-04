package com.rackspace.salus.telemetry.api.admin;

import com.rackspace.salus.telemetry.api.model.Profile;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class TopLevelApi {

  @GetMapping("profile")
  public Profile currentProfile(Authentication authentication) {
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
