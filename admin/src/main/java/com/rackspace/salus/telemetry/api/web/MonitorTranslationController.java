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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Slf4j
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class MonitorTranslationController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public MonitorTranslationController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/monitor-translations")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @RequestParam MultiValueMap<String, String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-translations")
        .queryParams(queryParams)
        .build()
        .toUriString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/monitor-translations/{id}")
  public ResponseEntity<?> getById(ProxyExchange<?> proxy, @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-translations/{id}")
        .build(id)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/monitor-translations")
  public ResponseEntity<?> create(ProxyExchange<?> proxy) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-translations")
        .build()
        .toUriString();

    return proxy.uri(backendUri).post();
  }

  @DeleteMapping("/monitor-translations/{id}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy, @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-translations/{id}")
        .build(id)
        .toString();

    return proxy.uri(backendUri).delete();
  }
}
