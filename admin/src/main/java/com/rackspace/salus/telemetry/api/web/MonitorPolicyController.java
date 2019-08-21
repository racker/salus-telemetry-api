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
import java.util.UUID;
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
public class MonitorPolicyController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public MonitorPolicyController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/policy-monitors")
  public ResponseEntity<?> getAllPolicyMonitors(ProxyExchange<?> proxy,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/policy-monitors")
        .queryParams(queryParams)
        .build()
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policy-monitors/{uuid}")
  public ResponseEntity<?> getAllPolicyMonitors(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/policy-monitors/{uuid}")
        .queryParams(queryParams)
        .build(uuid)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policies/monitors")
  public ResponseEntity<?> getAllMonitorPolicies(ProxyExchange<?> proxy,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors")
        .queryParams(queryParams)
        .build()
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policies/monitors/{uuid}")
  public ResponseEntity<?> getAllMonitorPolicies(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/{uuid}")
        .queryParams(queryParams)
        .build(uuid)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/policies/monitors")
  public ResponseEntity<?> create(ProxyExchange<?> proxy) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors")
        .build()
        .toString();

    return proxy.uri(backendUri)
        .post();
  }

  @DeleteMapping("/policies/monitors/{uuid}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
      @PathVariable UUID uuid) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/policy/monitors/{uuid}")
        .build(uuid)
        .toString();

    return proxy.uri(backendUri).delete();
  }
}