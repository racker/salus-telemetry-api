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

import com.rackspace.salus.event.manage.model.CreateTask;
import com.rackspace.salus.event.manage.model.CreateTaskResponse;
import com.rackspace.salus.event.manage.model.EventEngineTaskDTO;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.model.BackendRestException;
import com.rackspace.salus.telemetry.api.model.CreatedEventTask;
import com.rackspace.salus.telemetry.api.model.EventTaskScenario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FrontendEventTaskService {

  private final RestTemplate eventTaskRest;

  @Autowired
  public FrontendEventTaskService(ServicesProperties servicesProperties,
                                  RestTemplateBuilder restTemplateBuilder) {
    this.eventTaskRest = restTemplateBuilder
        .rootUri(servicesProperties.getEventManagementUrl())
        .build();
  }

  public CreatedEventTask create(String tenantId,
                                 String measurement,
                                 EventTaskScenario scenario) {

    final CreateTask createTask = new CreateTask()
        .setMeasurement(measurement);

    // GraphQL currently lacks unions on input types, so we need to do this dance
    if (scenario.getFalling() != null && createTask.getScenario() == null) {
      createTask.setScenario(scenario.getFalling());
    }
    else if (scenario.getRising() != null && createTask.getScenario() == null) {
      createTask.setScenario(scenario.getRising());
    }
    else {
      throw new IllegalArgumentException("One and only one scenario can be given");
    }

    final CreateTaskResponse response = eventTaskRest.postForObject(
        "/api/tasks/{tenantId}",
        createTask,
        CreateTaskResponse.class,
        tenantId
    );

    return new CreatedEventTask().setId(response.getId().toString());
  }

  public List<EventEngineTaskDTO> getAll(String tenantId) {
    final ResponseEntity<List<EventEngineTaskDTO>> response = eventTaskRest.exchange(
        "/api/tasks/{tenantId}",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<EventEngineTaskDTO>>() {
        },
        tenantId
    );

    if (response.getStatusCode().isError()) {
      throw new BackendRestException("Trying to get event tasks", response);
    }

    return response.getBody();
  }

  public void delete(String tenantId, String id) {
    eventTaskRest.delete(
        "/api/tasks/{tenantId}/{taskId}",
        tenantId, id
    );
  }
}
