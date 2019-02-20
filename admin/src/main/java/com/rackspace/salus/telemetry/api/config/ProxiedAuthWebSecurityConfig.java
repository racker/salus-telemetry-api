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

import com.rackspace.salus.common.web.PreAuthenticatedFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Profile("proxied-auth")
public class ProxiedAuthWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ApiAdminProperties properties;

  @Autowired
  public ProxiedAuthWebSecurityConfig(ApiAdminProperties properties) {
    this.properties = properties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .addFilterBefore(
            new PreAuthenticatedFilter(properties.getUserHeader(), properties.getGroupsHeader()),
            BasicAuthenticationFilter.class
        )
        .authorizeRequests()
        .antMatchers("/graphql")
        .hasAnyRole(properties.getRoles());
  }

}
