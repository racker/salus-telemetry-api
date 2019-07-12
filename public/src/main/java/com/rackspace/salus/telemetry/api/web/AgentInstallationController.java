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

package com.rackspace.salus.telemetry.api.web;

import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * NOTE: this controller's API will soon be moved to the internal-admin API to enable support
 * personnel to manage deployment of agent releases for their customers.
 */
@RestController
@Slf4j
public class AgentInstallationController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public AgentInstallationController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant/{tenantId}/agent-installs")
  public ResponseEntity<?> getAllAgentInstallations(ProxyExchange<?> proxy,
                                                    @PathVariable String tenantId,
                                                    @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/tenant/{tenantId}/agent-installs")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant/{tenantId}/agent-installs")
  public ResponseEntity<?> installAgentRelease(ProxyExchange<?> proxy, @PathVariable String tenantId) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/tenant/{tenantId}/agent-installs")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).post();
  }

  @DeleteMapping("/tenant/{tenantId}/agent-installs/{agentInstallId}")
  public ResponseEntity<?> uninstallAgentRelease(ProxyExchange<?> proxy,
                                                 @PathVariable String tenantId,
                                                 @PathVariable String agentInstallId) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/tenant/{tenantId}/agent-installs/{agentInstallId}")
        .build(tenantId, agentInstallId)
        .toString();

    return proxy.uri(backendUri).delete();
  }
}
