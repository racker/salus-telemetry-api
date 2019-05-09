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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Slf4j
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class MonitorsController {

  private final UserService userService;
  private final ServicesProperties servicesProperties;

  @Autowired
  public MonitorsController(UserService userService, ServicesProperties servicesProperties) {
    this.userService = userService;
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/monitors")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/monitors")
  public ResponseEntity<?> create(ProxyExchange<?> proxy) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/monitors/{id}")
  public ResponseEntity<?> update(ProxyExchange<?> proxy,
                                                      @PathVariable String id) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri)
        .put();
  }

  @DeleteMapping("/monitors/{id}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
                                  @PathVariable String id) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/boundMonitors")
  public ResponseEntity<?> getAllBoundMonitors(ProxyExchange<?> proxy,
                                               @RequestParam MultiValueMap<String,String> queryParams) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/boundMonitors")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

}
