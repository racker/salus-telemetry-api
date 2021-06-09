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

import com.rackspace.salus.common.config.IdentityConfig;
import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
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

  @GetMapping("/monitor-templates")
  public ResponseEntity<?> getAllMonitorTemplates(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-templates")
        .queryParams(queryParams)
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/monitor-templates/{uuid}")
  public ResponseEntity<?> getMonitorTemplate(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-templates/{uuid}")
        .queryParams(queryParams)
        .buildAndExpand(uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/monitor-templates")
  public ResponseEntity<?> createMonitorTemplates(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-templates")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).post();
  }

  @PutMapping("/monitor-templates/{uuid}")
  public ResponseEntity<?> updateMonitorTemplates(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-templates/{uuid}")
        .buildAndExpand(uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/monitor-templates/{uuid}")
  public ResponseEntity<?> deleteMonitorTemplates(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/admin/monitor-templates/{uuid}")
        .buildAndExpand(uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/policies/monitors")
  public ResponseEntity<?> getAllMonitorPolicies(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors")
        .queryParams(queryParams)
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policies/monitors/{uuid}")
  public ResponseEntity<?> getAllMonitorPolicies(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/{uuid}")
        .queryParams(queryParams)
        .buildAndExpand(uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/policies/monitors")
  public ResponseEntity<?> createPolicy(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri)
        .post();
  }

  @DeleteMapping("/policies/monitors/{uuid}")
  public ResponseEntity<?> deletePolicy(ProxyExchange<?> proxy,
      @PathVariable UUID uuid,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/{uuid}")
        .buildAndExpand(uuid)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).delete();
  }

  @PostMapping("/policies/monitors/opt-out")
  public ResponseEntity<?> optOutPolicy(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/opt-out")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri)
        .post();
  }

  @GetMapping("/policies/monitors/effective/{tenantId}")
  public ResponseEntity<?> getEffectivePoliciesByTenantId(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/effective/{tenantId}")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policies/monitors/effective/{tenantId}/monitor-ids")
  public ResponseEntity<?> getEffectiveMonitorIdsUsingMonitorPoliciesForTenant(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/effective/{tenantId}/monitor-ids")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/policies/monitors/effective/{tenantId}/policy-ids")
  public ResponseEntity<?> getEffectiveMonitorPolicyIdsForTenant(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestParam(required = false, defaultValue = "true") boolean includeNullMonitors,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/policy/monitors/effective/{tenantId}/policy-ids")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }
}
