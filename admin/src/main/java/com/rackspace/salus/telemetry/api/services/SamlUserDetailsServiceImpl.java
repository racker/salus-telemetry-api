package com.rackspace.salus.telemetry.api.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SamlUserDetailsServiceImpl implements SAMLUserDetailsService {

  @Override
  public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {

    final String userId = samlCredential.getNameID().getValue();

    final List<SimpleGrantedAuthority> authorities = Stream
        .of(samlCredential.getAttributeAsStringArray("http://schemas.xmlsoap.org/claims/Group"))
        .map(group ->
            group
                .replaceAll("\\W+", "_")
                .toUpperCase()
        )
        .map(normalized -> new SimpleGrantedAuthority("ROLE_" + normalized))
        .collect(Collectors.toList());

    return new User(userId, "",
        true, true, true, true,
        authorities);
  }
}
