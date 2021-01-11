/*
 * Copyright 2020 Rackspace US, Inc.
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
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Slf4j
@PreAuthorize("{#tenantId == authentication.principal}")
public class AgentHistoryController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public AgentHistoryController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant/{tenantId}/agent-history")
  public ResponseEntity<?> getAgentHistoryForTenant(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAmbassadorServiceUrl())
        .path("/api/tenant/{tenantId}/agent-history")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/agent-history/{uuid}")
  public ResponseEntity<?> getAgentHistoryById(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getAmbassadorServiceUrl())
        .path("/api/tenant/{tenantId}/agent-history/{uuid}")
        .queryParams(queryParams)
        .buildAndExpand(tenantId, uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }
}
