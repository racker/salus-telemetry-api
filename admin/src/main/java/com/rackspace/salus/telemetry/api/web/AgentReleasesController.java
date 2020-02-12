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

import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class AgentReleasesController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public AgentReleasesController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;

  }

  @GetMapping("/agent-releases")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @RequestHeader HttpHeaders headers,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/admin/agent-releases")
        .queryParams(queryParams)
        .build()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/agent-releases/{agentReleaseId}")
  public ResponseEntity<?> getOne(ProxyExchange<?> proxy,
                                  @PathVariable String agentReleaseId,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/admin/agent-releases/{agentReleaseId}")
        .build(agentReleaseId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/agent-releases")
  public ResponseEntity<?> declareAgentRelease(ProxyExchange<?> proxy,
                                               @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/admin/agent-releases")
        .build()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).post();
  }

  @DeleteMapping("/agent-releases/{agentReleaseId}")
  public ResponseEntity<?> deleteOne(ProxyExchange<?> proxy,
                                     @PathVariable String agentReleaseId,
                                     @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/admin/agent-releases/{agentReleaseId}")
        .build(agentReleaseId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }
}
