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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/api")
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class ZonesController {
  private final ServicesProperties servicesProperties;

  @Autowired
  public ZonesController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/zones/**")
  public ResponseEntity<?> get(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {
    final String zone = proxy.path("/api/zones/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zones/{name}")
        .queryParams(queryParams)
        .buildAndExpand(zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/zones")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zones")
        .queryParams(queryParams)
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .get();
  }

  @PostMapping("/zones")
  public ResponseEntity<?> create(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zones")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/zones/**")
  public ResponseEntity<?> update(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {
    final String zone = proxy.path("/api/zones/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zones/{name}")
        .buildAndExpand(zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .put();
  }

  @DeleteMapping("/zones/**")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {
    final String zone = proxy.path("/api/zones/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zones/{name}")
        .buildAndExpand(zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/zone-assignment-counts/**")
  public ResponseEntity<?> getPublicZoneAssignmentCountsForZone(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {
    String zone = proxy.path("/api/zone-assignment-counts/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zone-assignment-counts/{name}")
        .buildAndExpand(zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/zone-assignment-counts")
  public ResponseEntity<?> getAllPublicZoneAssignmentCounts(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/zone-assignment-counts")
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/rebalance-zone/**")
  public ResponseEntity<?> rebalancePublicZone(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {
    String zone = proxy.path("/api/rebalance-zone/");

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/rebalance-zone/{name}")
        .buildAndExpand(zone)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        // no body needed for this operation, but proxy wants to resolve one
        .body("")
        .post();
  }

}
