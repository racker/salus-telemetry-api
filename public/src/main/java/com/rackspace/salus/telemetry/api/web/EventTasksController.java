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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class EventTasksController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public EventTasksController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @PostMapping("/tenant/{tenantId}/event-tasks")
  public ResponseEntity<?> create(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tenant/{tenantId}/tasks")
        .build(tenantId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).post();
  }

  @GetMapping("/tenant/{tenantId}/event-tasks")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tenant/{tenantId}/tasks")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/event-tasks/{taskId}")
  public ResponseEntity<?> getOne(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @PathVariable String taskId,
      @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tenant/{tenantId}/tasks/{taskId}")
        .build(tenantId, taskId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @DeleteMapping("/tenant/{tenantId}/event-tasks/{taskId}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String taskId,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tenant/{tenantId}/tasks/{taskId}")
        .build(tenantId, taskId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }

  @PostMapping("/tenant/{tenantId}/test-event-task")
  public ResponseEntity<?> testEventTask(ProxyExchange<?> proxy,
                                         @PathVariable String tenantId,
                                         @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getEventManagementUrl())
        .path("/api/tenant/{tenantId}/test-task")
        .build(tenantId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).post();
  }
}
