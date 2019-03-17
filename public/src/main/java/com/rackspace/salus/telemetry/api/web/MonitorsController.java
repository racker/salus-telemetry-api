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
import com.rackspace.salus.telemetry.api.model.DetailedMonitorInput;
import com.rackspace.salus.telemetry.api.model.DetailedMonitorOutput;
import com.rackspace.salus.telemetry.api.services.MonitorConversionService;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.model.Monitor;
import com.rackspace.salus.telemetry.model.PagedContent;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final MonitorConversionService monitorConversionService;

  @Autowired
  public MonitorsController(UserService userService, ServicesProperties servicesProperties,
                            MonitorConversionService monitorConversionService) {
    this.userService = userService;
    this.servicesProperties = servicesProperties;
    this.monitorConversionService = monitorConversionService;
  }

  @GetMapping("/monitors")
  public ResponseEntity<PagedContent<DetailedMonitorOutput>> getAll(ProxyExchange<PagedContent<Monitor>> proxy,
                                                                    @RequestParam(defaultValue = "100") int size,
                                                                    @RequestParam(defaultValue = "0") int page) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .queryParam("size", size)
        .queryParam("page", page)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get(responseEntity -> {

      if (responseEntity.getBody() == null) {
        log.warn("Got null response body for request={}", backendUri);
        return ResponseEntity.noContent()
            .headers(responseEntity.getHeaders())
            .build();
      }

      final PagedContent<DetailedMonitorOutput> responsePage = responseEntity.getBody()
          .map(monitorConversionService::convertToOutput);

      return ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responsePage);
    });
  }

  @PostMapping("/monitors")
  public ResponseEntity<DetailedMonitorOutput> create(ProxyExchange<Monitor> proxy,
                                                      @RequestBody @Valid DetailedMonitorInput input) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri)
        .body(monitorConversionService.convertFromInput(input))
        .post(responseEntity -> {

      return ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(monitorConversionService.convertToOutput(responseEntity.getBody()));
    });
  }

  @PutMapping("/monitors/{id}")
  public ResponseEntity<DetailedMonitorOutput> update(ProxyExchange<Monitor> proxy,
                                                      @PathVariable String id,
                                                      @RequestBody @Valid DetailedMonitorInput input) {
    final String tenantId = userService.currentTenantId();

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri)
        .body(monitorConversionService.convertFromInput(input))
        .put(responseEntity -> {

          return ResponseEntity.status(responseEntity.getStatusCode())
              .headers(responseEntity.getHeaders())
              .body(monitorConversionService.convertToOutput(responseEntity.getBody()));
        });
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
}
