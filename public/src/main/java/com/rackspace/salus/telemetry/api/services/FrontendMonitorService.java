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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rackspace.salus.monitor_management.web.model.MonitorCreate;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.model.BackendRestException;
import com.rackspace.salus.telemetry.api.model.CreatedMonitor;
import com.rackspace.salus.telemetry.api.model.LocalMonitorConfigs;
import com.rackspace.salus.telemetry.api.model.MonitorConfigs;
import com.rackspace.salus.telemetry.api.model.RetrievedMonitor;
import com.rackspace.salus.telemetry.api.model.telegraf.UsingLocalTelegraf;
import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.ConfigSelectorScope;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.Monitor;
import com.rackspace.salus.telemetry.model.PagedContent;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This service enables the public API to interface with the backend REST services for monitor management.
 */
@Service
@Slf4j
public class FrontendMonitorService {

  private final ObjectMapper objectMapper;
  private final RestTemplate monitorManagementRest;

  @Autowired
  public FrontendMonitorService(ObjectMapper objectMapper,
                                RestTemplateBuilder restTemplateBuilder,
                                ServicesProperties servicesProperties) {
    this.objectMapper = objectMapper;
    this.monitorManagementRest = restTemplateBuilder
        .rootUri(servicesProperties.getMonitorManagementUrl())
        .build();
  }

  public CreatedMonitor createMonitor(String tenantId, LocalMonitorConfigs configs,
                                      List<Label> matchingLabels) {

    if (configs.getUsingTelegraf() != null) {
      return createTelegrafMonitor(tenantId, configs.getUsingTelegraf(), matchingLabels);
    } else {
      throw new IllegalArgumentException("Configuration is missing agent-specific parts");
    }

  }

  private CreatedMonitor createTelegrafMonitor(String tenantId, UsingLocalTelegraf configs,
                                               List<Label> matchingLabels) {
    final String configJson;

    try {
      configJson = objectMapper.writeValueAsString(configs);
    } catch (JsonProcessingException e) {
      log.warn("Failed to encode configs={} for tenant={}", configs, tenantId, e);
      throw new IllegalStateException("Unable to serialize agent configuration");
    }

    log.debug("Converted graphql config={} into json={}", configs, configJson);

    final MonitorCreate monitorCreate = new MonitorCreate()
        .setAgentType(AgentType.TELEGRAF)
        .setSelectorScope(ConfigSelectorScope.ALL_OF)
        .setLabels(Label.convertToMap(matchingLabels))
        .setContent(configJson);

    final Monitor response = monitorManagementRest
        .postForObject("/api/tenant/{tenantId}/monitors", monitorCreate, Monitor.class, tenantId);

    return new CreatedMonitor().setId(response.getId().toString());
  }

  public PagedContent<RetrievedMonitor> retrieve(String tenantId, int size, int page) {

    final ResponseEntity<PagedContent<Monitor>> response = monitorManagementRest
        .exchange(
            "/api/tenant/{tenantId}/monitors?size={size}&page={page}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PagedContent<Monitor>>() {
            },
            tenantId,
            size,
            page
        );

    if (response.getStatusCode().isError()) {
      log.warn("Failed to retrieve monitors for tenant={}, response={}", tenantId, response);
      throw new BackendRestException("Failed to retrieve monitors", response);
    }

    return response.getBody().map(this::convertToRetrieveMonitor);
  }

  public String convertToJson(UsingLocalTelegraf usingLocalTelegraf) {
    final String configJson;

    try {
      configJson = objectMapper.writeValueAsString(usingLocalTelegraf);
    } catch (JsonProcessingException e) {
      log.warn("Failed to encode configs={}", usingLocalTelegraf, e);
      throw new IllegalStateException("Unable to serialize agent configuration");
    }

    return configJson;
  }

  private RetrievedMonitor convertToRetrieveMonitor(Monitor monitor) {
    final RetrievedMonitor retrievedMonitor = new RetrievedMonitor()
        .setId(monitor.getId().toString())
        .setMatchingLabels(Label.convertToLabelList(monitor.getLabels()));

    if (monitor.getSelectorScope() == ConfigSelectorScope.ALL_OF) {
      if (monitor.getAgentType() == AgentType.TELEGRAF) {
        try {
          retrievedMonitor.setConfigs(new MonitorConfigs()
              .setLocal(
                  new LocalMonitorConfigs().setUsingTelegraf(
                      objectMapper.readValue(monitor.getContent(), UsingLocalTelegraf.class)
                  )
              )
          );
        } catch (IOException e) {
          log.warn("Failed to deserialize into UsingLocalTelegraf: {}", monitor.getContent(), e);
        }
      } else {
        log.warn(
            "Unsupported scope={}, agentType={}", monitor.getSelectorScope(),
            monitor.getAgentType()
        );
      }
    }

    return retrievedMonitor;
  }

  public void delete(String tenantId, String id) {
    monitorManagementRest.delete("/api/tenant/{tenantId}/monitors/{uuid}", tenantId, id);
  }

  public RetrievedMonitor retrieveOne(String tenantId, String id) {

    final Monitor monitor = monitorManagementRest.getForObject(
        "/api/tenant/{tenantId}/monitors/{uuid}",
        Monitor.class,
        tenantId, id
    );

    return convertToRetrieveMonitor(monitor);
  }
}
