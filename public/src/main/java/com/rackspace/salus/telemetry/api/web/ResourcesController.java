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
 *
 */

package com.rackspace.salus.telemetry.api.web;

import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import java.io.UnsupportedEncodingException;
import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Uses a Spring Cloud <a href="https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.1.1.RELEASE/multi/multi__building_a_simple_gateway_using_spring_mvc_or_webflux.html#_building_a_simple_gateway_using_spring_mvc_or_webflux">ProxyExchange</a>
 * to proxy calls to the resource-management service.
 */
@RestController
@Slf4j
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class ResourcesController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public ResourcesController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant/{tenantId}/resources")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/resources/{resourceId}")
  public ResponseEntity<?> getOne(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @PathVariable String resourceId) throws UnsupportedEncodingException {

    URI backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources/{resourceId}")
        .buildAndExpand(tenantId, resourceId)
        .toUri();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/resources-by-label/{logicalOperator}")
  public ResponseEntity<?> getResourcesWithLabels(ProxyExchange<?> proxy,
                                                  @PathVariable String tenantId,
                                                  @PathVariable String logicalOperator,
                                                  @RequestHeader HttpHeaders headers,
                                                  @RequestParam Map<String, String> labels) {
    final UriComponentsBuilder builder = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources-by-label/{logicalOperator}");

    labels.forEach(builder::queryParam);

    final String backendUri = builder
        .buildAndExpand(tenantId, logicalOperator)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/resource-labels")
  public ResponseEntity<?> getResourceLabels(ProxyExchange<?> proxy,
                                             @PathVariable String tenantId,
                                             @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resource-labels")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/resource-metadata-keys")
  public ResponseEntity<?> getResourceMetadataKeys(ProxyExchange<?> proxy,
                                                   @PathVariable String tenantId,
                                                   @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resource-metadata-keys")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/resource-label-namespaces")
  public ResponseEntity<?> getLabelNamespaces(ProxyExchange<?> proxy,
                                              @PathVariable String tenantId,
                                              @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resource-label-namespaces")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant/{tenantId}/resources")
  public ResponseEntity<?> create(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestHeader HttpHeaders headers) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources")
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).post();
  }

  @PutMapping("/tenant/{tenantId}/resources/{resourceId}")
  public ResponseEntity<?> update(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String resourceId,
                                  @RequestHeader HttpHeaders headers) {

    final URI backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources/{resourceId}")
        .buildAndExpand(tenantId, resourceId)
        .toUri();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).put();
  }

  @DeleteMapping("/tenant/{tenantId}/resources/{resourceId}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String resourceId,
                                  @RequestHeader HttpHeaders headers) {

    final URI backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/resources/{resourceId}")
        .buildAndExpand(tenantId, resourceId)
        .toUri();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/tenant/{tenantId}/resources-search/")
  public ResponseEntity<?> searchResources(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @RequestHeader HttpHeaders headers,
      @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/tenant/{tenantId}/search")
        .queryParams(queryParams)
        .buildAndExpand(tenantId)
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }
}
