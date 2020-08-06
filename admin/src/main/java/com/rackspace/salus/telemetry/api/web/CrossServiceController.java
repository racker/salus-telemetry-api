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

import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import java.net.URI;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @DeleteMapping("/{tenantId}")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @PathVariable String tenantId) {





    // delete agent installs
    final String agentCatalogManagementURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getAgentCatalogManagementUrl())
        .path("/admin/tenant/{tenantId}/zones")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> agentInstalls = proxy.uri(agentCatalogManagementURI).get();

    // delete envoy tokens
    final String authURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getAuthServiceUrl())
        .path("/admin/tenant/{tenantId}/envoy-tokens")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> envoyTokens = proxy.uri(authURI).get();

    // delete event engine tasks
    final String eventEngineURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventEngineUrl())
        .path("/admin/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> eventResponse = proxy.uri(eventEngineURI).get();

    // Delete from resources first
    final String resourcesURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/admin/tenant/{tenantId}/resources")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> resource = proxy.uri(resourcesURI).get();

    // Delete policies
    final String policyURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/admin/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> policies = proxy.uri(policyURI).get();

    // Delete monitors
    final String monitorsURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/admin/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> monitor = proxy.uri(monitorsURI).get();

    // delete tenant metadata -- I think this already exists
    final String tenantMetadataURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata/{tenantId}")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> tenantMetadata = proxy.uri(tenantMetadataURI).get();

    // delete zones
    final String zonesURI = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/admin/tenant/{tenantId}/zones")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    ResponseEntity<?> zone = proxy.uri(zonesURI).get();
    URI location = URI.create(zonesURI);
    if(zone.getStatusCode().isError() || tenantMetadata.getStatusCode().isError() || monitor.getStatusCode().isError()
      || policies.getStatusCode().isError() || resource.getStatusCode().isError() || eventResponse.getStatusCode().isError()
      || envoyTokens.getStatusCode().isError() || agentInstalls.getStatusCode().isError()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST_400).headers(headers).body(zone.getBody());
    }

    return null;
  }

}
