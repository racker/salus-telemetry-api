/*
 * Copyright 2019 Rackspace US, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rackspace.salus.telemetry.api.config;

import com.rackspace.salus.common.web.ReposeHeaderFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Slf4j
@Profile("!unsecured")
public class TenantWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ApiAdminProperties apiAdminProperties;

  @Autowired
  public TenantWebSecurityConfig(ApiAdminProperties apiAdminProperties) {
    this.apiAdminProperties = apiAdminProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    log.debug("Configuring tenant web security to authorize roles: {}", apiAdminProperties.getRoles());
    http
        .csrf().disable()
        .addFilterBefore(
            new ReposeHeaderFilter(),
            BasicAuthenticationFilter.class
        )
        .authorizeRequests()
        .antMatchers("/api/**")
        .hasAnyRole(apiAdminProperties.getRoles());
  }
}
