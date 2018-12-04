package com.rackspace.salus.telemetry.api.config;

import com.rackspace.salus.common.web.ReposeHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Profile("secured")
public class TenantWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ApiPublicProperties apiPublicProperties;

  @Autowired
  public TenantWebSecurityConfig(ApiPublicProperties apiPublicProperties) {
    this.apiPublicProperties = apiPublicProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .addFilterBefore(
            new ReposeHeaderFilter(),
            BasicAuthenticationFilter.class
        )
        .authorizeRequests()
        .antMatchers("/api/**")
        .hasAnyRole(apiPublicProperties.getRoles());
  }
}
