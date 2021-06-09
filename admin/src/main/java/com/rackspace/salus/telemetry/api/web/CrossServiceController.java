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
 *
 */

package com.rackspace.salus.telemetry.api.web;

import com.rackspace.salus.common.config.IdentityConfig;
import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class CrossServiceController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public CrossServiceController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;

  }

  @DeleteMapping("/tenant/{tenantId}")
  public ResponseEntity<?> deleteAll(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String, String> queryParams,
      @PathVariable String tenantId,
      @RequestParam(defaultValue = "true") boolean removeTenantMetadata,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {
    queryParams.add("sendEvents", "false");
    List bodies = new LinkedList();

    // delete agent installs
    final String agentCatalogManagementURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/api/admin/tenant/{tenantId}/agent-installs")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    ResponseEntity<?> agentInstalls = proxy.uri(agentCatalogManagementURI).delete();
    if (agentInstalls.getStatusCode().isError()) {
      bodies.add(agentInstalls.getBody());
    }

    // delete envoy tokens
    final String authURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getAuthServiceUrl())
        .path("/api/admin/tenant/{tenantId}/envoy-tokens")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    ResponseEntity<?> envoyTokens = proxy.uri(authURI).delete();

    if (envoyTokens.getStatusCode().isError()) {
      bodies.add(envoyTokens.getBody());
    }

    // delete event engine tasks
    final String eventEngineURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/admin/tenant/{tenantId}/tasks")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ResponseEntity<?> eventResponse = proxy.uri(eventEngineURI).delete();

    if (eventResponse.getStatusCode().isError()) {
      bodies.add(eventResponse.getBody());
    }

    // Delete from resources first
    final String resourcesURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/admin/tenant/{tenantId}/resources")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ResponseEntity<?> resource = proxy.uri(resourcesURI).delete();

    if (resource.getStatusCode().isError()) {
      bodies.add(resource.getBody());
    }

    // Delete monitors
    final String monitorsURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ResponseEntity<?> monitor = proxy.uri(monitorsURI).delete();

    if (monitor.getStatusCode().isError()) {
      bodies.add(monitor.getBody());
    }

    // delete tenant metadata
    if (removeTenantMetadata) {
      final String tenantMetadataURI = UriComponentsBuilder
          .fromUriString(servicesProperties.getPolicyManagementUrl())
          .path("/api/admin/tenant-metadata/{tenantId}")
          .queryParams(queryParams)
          .buildAndExpand(tenantId)
          .toUriString();

      ResponseEntity<?> tenantMetadata = proxy.uri(tenantMetadataURI).delete();

      if (tenantMetadata.getStatusCode().isError()) {
        bodies.add(tenantMetadata.getBody());
      }
    }

    // delete zones
    final String zonesURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/tenant/{tenantId}/zones")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ResponseEntity<?> zone = proxy.uri(zonesURI).delete();

    if (zone.getStatusCode().isError()) {
      bodies.add(zone.getBody());
    }

    if (!bodies.isEmpty()) {
      LinkedHashMap responseBody = new LinkedHashMap();

      responseBody.put("messages", bodies);

      return ResponseEntity.status(HttpStatus.CONFLICT_409).headers(headers).body(responseBody);
    }

    return ResponseEntity.status(HttpStatus.ACCEPTED_202).headers(headers).build();
  }


}
