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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/eventTasks")
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class EventTasksController {

  private final UserService userService;
  private final ServicesProperties servicesProperties;

  @Autowired
  public EventTasksController(UserService userService, ServicesProperties servicesProperties) {
    this.userService = userService;
    this.servicesProperties = servicesProperties;
  }

  @PostMapping
  public ResponseEntity<?> create(ProxyExchange<?> proxy) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tasks/{tenantId}")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).post();
  }

  @GetMapping
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tasks/{tenantId}")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> getOne(ProxyExchange<?> proxy,
                                  @PathVariable String taskId) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tasks/{tenantId}/{taskId}")
        .build(tenantId, taskId)
        .toString();

    return proxy.uri(backendUri).delete();

  }
}