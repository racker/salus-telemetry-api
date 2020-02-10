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

import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Slf4j
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class MonitorsController {
  private static final String PATCH_MEDIA_TYPE_VALUE = "application/json-patch+json";

  private final ServicesProperties servicesProperties;

  @Autowired
  public MonitorsController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/tenant/{tenantId}/monitors")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/monitors/{id}")
  public ResponseEntity<?> get(ProxyExchange<?> proxy,
      @PathVariable String tenantId,
      @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @PostMapping("/tenant/{tenantId}/monitors")
  public ResponseEntity<?> create(ProxyExchange<?> proxy, @PathVariable String tenantId) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri)
        .post();
  }

  @PutMapping("/tenant/{tenantId}/monitors/{id}")
  public ResponseEntity<?> update(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri)
        .put();
  }

  /**
   * Proxy exchange does not work with PATCH so we must reconstruct the request instead.
   *
   * Related issue: https://github.com/spring-cloud/spring-cloud-netflix/issues/1777
   *
   * @param body The request body to pass along
   * @param tenantId The tenant id from the url path
   * @param id The monitor id to update from the url path
   * @return The String deserialization of the monitor.
   */
  @PatchMapping(path = "/tenant/{tenantId}/monitors/{id}",
                consumes = PATCH_MEDIA_TYPE_VALUE)
  public ResponseEntity<String> patch(@RequestHeader MultiValueMap<String, String> originalHeaders,
                                 @RequestBody String body,
                                 @PathVariable String tenantId,
                                 @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    HttpHeaders headers = new HttpHeaders(originalHeaders);
    HttpEntity<String> entity = new HttpEntity<>(body, headers);
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    RestTemplate restTemplate = new RestTemplate(requestFactory);

    return restTemplate.exchange(backendUri, HttpMethod.PATCH, entity, String.class);
  }

  @DeleteMapping("/tenant/{tenantId}/monitors/{id}")
  public ResponseEntity<?> delete(ProxyExchange<?> proxy,
                                  @PathVariable String tenantId,
                                  @PathVariable String id) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitors/{uuid}")
        .build(tenantId, id)
        .toString();

    return proxy.uri(backendUri).delete();
  }

  @GetMapping("/tenant/{tenantId}/monitor-label-selectors")
  public ResponseEntity<?> getMonitorLabelSelectors(ProxyExchange<?> proxy,
                                                    @PathVariable String tenantId) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitor-label-selectors")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/monitor-plugins-schema")
  public ResponseEntity<?> getMonitorPluginsSchema(ProxyExchange<?> proxy,
                                                   @PathVariable String tenantId) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/monitor-plugins-schema")
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

  @GetMapping("/tenant/{tenantId}/bound-monitors")
  public ResponseEntity<?> getAllBoundMonitors(ProxyExchange<?> proxy,
                                               @PathVariable String tenantId,
                                               @RequestParam MultiValueMap<String,String> queryParams) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getMonitorManagementUrl())
        .path("/api/tenant/{tenantId}/bound-monitors")
        .queryParams(queryParams)
        .build(tenantId)
        .toString();

    return proxy.uri(backendUri).get();
  }

}
