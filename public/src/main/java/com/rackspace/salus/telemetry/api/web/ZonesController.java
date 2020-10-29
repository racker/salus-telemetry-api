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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class ZonesController {
  private final ServicesProperties servicesProperties;

  @Autowired
  public ZonesController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant/{tenantId}/zones")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zones")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/zones/**")
  public ResponseEntity<?> get(ProxyExchange<?> proxy,
                               @PathVariable String tenantId,
                               @RequestHeader HttpHeaders headers) {
    final String zone = proxy.path("/api/zones/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zones/{name}")
        .buildAndExpand(tenantId, zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant/{tenantId}/zones")
  public ResponseEntity<?> create(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zones")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/tenant/{tenantId}/zones/{name}")
  public ResponseEntity<?> update(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String name,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zones/{name}")
        .buildAndExpand(tenantId, name)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .put();
  }

  @DeleteMapping("/tenant/{tenantId}/zones/{name}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String name,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zones/{name}")
        .buildAndExpand(tenantId, name)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/tenant/{tenantId}/zone-assignment-counts/{name}")
  public ResponseEntity<?> getPrivateZoneAssignmentCountsForZone(ProxyExchange<?> proxy,
                                                          @PathVariable String tenantId,
                                                          @PathVariable String name,
                                                          @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zone-assignment-counts/{name}")
        .buildAndExpand(tenantId, name)
        .toUriString();
    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/zone-assignment-counts")
  public ResponseEntity<?> getAllPrivateZoneAssignmentCounts(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/zone-assignment-counts")
        .buildAndExpand(tenantId)
        .toUriString();
    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant/{tenantId}/rebalance-zone/{name}")
  public ResponseEntity<?> rebalancePrivateZone(ProxyExchange<?> proxy,
                                                @PathVariable String tenantId,
                                                @PathVariable String name,
                                                @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/rebalance-zone/{name}")
        .buildAndExpand(tenantId, name)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        // no body needed for this operation, but proxy wants to resolve one
        .body("")
        .post();

  }
}
