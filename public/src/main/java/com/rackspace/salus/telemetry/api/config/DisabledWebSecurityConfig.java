package com.rackspace.salus.telemetry.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Profile("unsecured")
public class DisabledWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors().disable()
        .addFilterBefore(new DummyAttributeSecurityConfig(), BasicAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest().permitAll();
  }
}
