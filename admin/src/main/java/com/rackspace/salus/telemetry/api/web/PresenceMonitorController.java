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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/api")
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class PresenceMonitorController {

  private final ServicesProperties servicesProperties;

  @Autowired
  public PresenceMonitorController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/presence-monitor/partitions")
  public ResponseEntity<?> getPresenceMonitorPartitions(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPresenceMonitorUrl())
        .path("/api/admin/presence-monitor/partitions")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).get();
  }

  @PutMapping("/presence-monitor/partitions")
  public ResponseEntity<?> changePresenceMonitorPartitions(ProxyExchange<?> proxy,
      @RequestHeader HttpHeaders headers,
      @RequestAttribute(IdentityConfig.ATTRIBUTE_NAME) Map<String, Object> attributes) {
    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getPresenceMonitorUrl())
        .path("/api/admin/presence-monitor/partitions")
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers, attributes);

    return proxy.uri(backendUri).put();
  }
}