package com.rackspace.salus.telemetry.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Profile("!secured")
@EnableConfigurationProperties(DevProperties.class)
public class DisabledWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final DevProperties devProperties;

  @Autowired
  public DisabledWebSecurityConfig(DevProperties devProperties) {
    this.devProperties = devProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors().disable()
        // set a specific principal so we can mimic a tenant's ID in dev
        .anonymous().principal(devProperties.getAnonymousUsername())
        .and()
        .authorizeRequests()
        .anyRequest().permitAll();
  }
}
