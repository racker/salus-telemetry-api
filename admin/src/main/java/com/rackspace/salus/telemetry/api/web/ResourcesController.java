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

import com.rackspace.salus.common.util.ApiUtils;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@SuppressWarnings("Duplicates") // due to repetitive proxy setup/calls
public class ResourcesController {
  private final ServicesProperties servicesProperties;

  @Autowired
  public ResourcesController(ServicesProperties servicesProperties) {
    this.servicesProperties = servicesProperties;
  }

  @GetMapping("/resources")
  public ResponseEntity<?> getAll(ProxyExchange<?> proxy,
                           @RequestHeader HttpHeaders headers,
                           @RequestParam MultiValueMap<String,String> queryParams) {

    final String backendUri = UriComponentsBuilder
        .fromUriString(servicesProperties.getResourceManagementUrl())
        .path("/api/admin/resources")
        .queryParams(queryParams)
        .buildAndExpand()
        .toUriString();

    ApiUtils.applyRequiredHeaders(proxy, headers);

    return proxy.uri(backendUri).get();
  }
}
