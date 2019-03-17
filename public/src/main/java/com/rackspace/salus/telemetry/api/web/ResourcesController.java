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
import com.rackspace.salus.telemetry.api.services.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Uses a Spring Cloud <a href="https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.1.1.RELEASE/multi/multi__building_a_simple_gateway_using_spring_mvc_or_webflux.html#_building_a_simple_gateway_using_spring_mvc_or_webflux">ProxyExchange</a>
 * to proxy calls to the resource-management service.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class ResourcesController {

  private final UserService userService;
  private final ServicesProperties servicesProperties;

  @Autowired
  public ResourcesController(UserService userService, ServicesProperties servicesProperties) {
    this.userService = userService;
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/resources")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @RequestParam(defaultValue = "100") int size,
                                  @RequestParam(defaultValue = "0") int page) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources")
        .queryParam("size", size)
        .queryParam("page", page)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/resourcesByLabel")
  public ResponseEntity<?> getResourcesWithLabels(ProxyExchange<?> proxy,
                                                  @RequestParam Map<String, String> labels) {
    final String tenantId = userService.currentTenantId();

    final UriComponentsBuilder builder = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resourceLabels");

    labels.forEach(builder::queryParam);

    final String backendUri = builder
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/resources")
  public ResponseEntity<?> create(ProxyExchange<?> proxy) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).post();
  }

  @PutMapping("/resources/{resourceId}")
  public ResponseEntity<?> update(ProxyExchange<?> proxy, @PathVariable String resourceId) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/{tenantId}/resources/{resourceId}")
        .build(tenantId, resourceId)
        .toString();

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/resources/{resourceId}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy, @PathVariable String resourceId) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources/{resourceId}")
        .build(tenantId, resourceId)
        .toString();

    return proxy.uri(backendUri).delete();
  }
}