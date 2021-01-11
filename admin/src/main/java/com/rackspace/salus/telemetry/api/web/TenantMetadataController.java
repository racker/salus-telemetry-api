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
import java.util.Map;
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
public class TenantMetadataController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public TenantMetadataController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant-metadata")
  public ResponseEntity<?> getAllTenantMetadata(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata")
        .queryParams(queryParams)
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant-metadata/{tenantId}")
  public ResponseEntity<?> getTenantMetadata(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata/{tenantId}")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant-metadata")
  public ResponseEntity<?> createTenantMetadata(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).post();
  }

  @PutMapping("/tenant-metadata/{tenantId}")
  public ResponseEntity<?> upsertTenantMetadata(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata/{tenantId}")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/tenant-metadata/{tenantId}")
  public ResponseEntity<?> deleteTenantMetadata(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute("identityHeadersMap") Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPolicyManagementUrl())
        .path("/api/admin/tenant-metadata/{tenantId}")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).delete();
  }

}
