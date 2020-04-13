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
import com.rackspace.salus.telemetry.model.MonitorType;
import com.rackspace.salus.telemetry.model.TargetClassName;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Slf4j
public class MonitorMetadataPolicyController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public MonitorMetadataPolicyController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/policy/metadata/monitor")
  public ResponseEntity<?> getAllMetadataPoliciesForMonitors(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor")
        .queryParams(queryParams)
        .build()
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policy/metadata/monitor/{uuid}")
  public ResponseEntity<?> getMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor/{uuid}")
        .queryParams(queryParams)
        .build(uuid)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/policy/metadata/monitor")
  public ResponseEntity<?> createMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor")
        .build()
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/policy/metadata/monitor/{uuid}")
  public ResponseEntity<?> updateMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor/{uuid}")
        .build(uuid)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/policy/metadata/monitor/{uuid}")
  public ResponseEntity<?> deleteMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor/{uuid}")
        .build(uuid)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/policy/metadata/monitor/effective/{tenantId}")
  public ResponseEntity<?> getEffectiveMetadataPolicyForMonitorsByTenant(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor/effective/{tenantId}")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policy/metadata/monitor/effective/{tenantId}/{className}/{monitorType}")
  public ResponseEntity<?> getEffectiveMetadataPolicyForMonitorTypeByTenant(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @PathVariable TargetClassName className,
      @PathVariable MonitorType monitorType,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/monitor/effective/{tenantId}/{className}/{monitorType}")
        .queryParams(queryParams)
        .build(tenantId, className, monitorType)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/policy/metadata/zones")
  public ResponseEntity<?> createMetadataPolicyForZone(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/zones")
        .build()
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/policy/metadata/zones/{region}")
  public ResponseEntity<?> updateMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @PathVariable String region,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/zones/{region}")
        .build(region)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/policy/metadata/zones/{region}")
  public ResponseEntity<?> deleteMetadataPolicyForMonitors(ProxyExchange<?> proxy,
      @PathVariable String region,
      @RequestHeader HttpHeaders headers) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/metadata/zones/{region}")
        .build(region)
        .toString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }
}
