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
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskRequest;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse;
import com.rackspace.salus.telemetry.api.services.TestMonitorAndEventTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class TestMonitorController {

  private final ServicesProperties servicesProperties;
  private final TestMonitorAndEventTaskService testMonitorAndEventTaskService;

  @Autowired
  public TestMonitorController(ServicesProperties servicesProperties,
      TestMonitorAndEventTaskService testMonitorAndEventTaskService) {
    this.testMonitorAndEventTaskService = testMonitorAndEventTaskService;
    this.servicesProperties = servicesProperties;
  }

  @PostMapping("/tenant/{tenantId}/test-monitor")
  public ResponseEntity<?> create(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/test-monitor")
        .build(tenantId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .post();
  }

  @ResponseStatus(HttpStatus.FOUND)
  @PostMapping("/tenant/{tenantId}/test-monitor-event-task")
  public TestMonitorAndEventTaskResponse createTestMonitorAndEventTask(
      @PathVariable String tenantId,
      @RequestBody TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest) {
    return testMonitorAndEventTaskService
        .getTestMonitorAndEventTask(tenantId, testMonitorAndEventTaskRequest);
  }

}
