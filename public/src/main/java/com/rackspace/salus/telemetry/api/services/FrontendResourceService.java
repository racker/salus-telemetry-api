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

package com.rackspace.salus.telemetry.api.services;

import com.rackspace.salus.resource_management.web.model.ResourceCreate;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.model.BackendRestException;
import com.rackspace.salus.telemetry.api.model.CreatedResource;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.PagedContent;
import com.rackspace.salus.telemetry.model.Resource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontendResourceService {

  private final RestTemplate resourceManagementRest;

  @Autowired
  public FrontendResourceService(ServicesProperties servicesProperties,
                                 RestTemplateBuilder restTemplateBuilder) {
    this.resourceManagementRest = restTemplateBuilder
        .rootUri(servicesProperties.getResourceManagementUrl())
        .build();
  }

  public CreatedResource create(String tenantId, String resourceId, List<Label> labels) {

    final ResourceCreate resourceCreate = new ResourceCreate()
        .setResourceId(resourceId)
        .setLabels(Label.convertToMap(labels))
        .setPresenceMonitoringEnabled(true);

    final ResponseEntity<Resource> response = resourceManagementRest.postForEntity(
        "/api/tenant/{tenantId}/resources",
        resourceCreate,
        Resource.class,
        tenantId
    );

    if (response.getStatusCode().isError()) {
      throw new BackendRestException("Trying to create resource", response);
    }

    return new CreatedResource()
        .setId(response.getBody().getId())
        .setResourceId(resourceId);
  }

  public void delete(String tenantId, String resourceId) {
    resourceManagementRest.delete(
        "/api/tenant/{tenantId}/resources/{resourceId}",
        tenantId, resourceId
    );
  }

  public PagedContent<Resource> getAll(String tenantId,
                                       int size, int page) {

    final ResponseEntity<PagedContent<Resource>> response = resourceManagementRest.exchange(
        "/api/tenant/{tenantId}/resources?size={size}&page={page}",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<PagedContent<Resource>>() {
        },
        tenantId, size, page
    );

    if (response.getStatusCode().isError()) {
      throw new BackendRestException("Trying to get page of resources", response);
    }

    return response.getBody();
  }

  public Resource getOne(String tenantId, String resourceId) {

    return resourceManagementRest.getForObject(
        "/api/tenant/{tenantId}/resources/{resourceId}",
        Resource.class,
        tenantId, resourceId
    );
  }
}
