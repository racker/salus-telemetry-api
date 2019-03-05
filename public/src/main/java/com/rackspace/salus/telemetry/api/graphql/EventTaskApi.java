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

package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.event.manage.model.scenarios.Falling;
import com.rackspace.salus.event.manage.model.scenarios.Rising;
import com.rackspace.salus.event.manage.model.scenarios.Scenario;
import com.rackspace.salus.telemetry.api.model.CreatedEventTask;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.model.EventTaskScenario;
import com.rackspace.salus.telemetry.api.model.RetrievedEventEngineTask;
import com.rackspace.salus.telemetry.api.services.FrontendEventTaskService;
import com.rackspace.salus.telemetry.api.services.UserService;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@GraphQLApi
@Service
public class EventTaskApi {

  private final FrontendEventTaskService eventTaskService;
  private final UserService userService;

  @Autowired
  public EventTaskApi(FrontendEventTaskService eventTaskService, UserService userService) {
    this.eventTaskService = eventTaskService;
    this.userService = userService;
  }

  @GraphQLMutation
  public CreatedEventTask createEventTask(@GraphQLNonNull String measurement,
                                          @GraphQLNonNull EventTaskScenario scenario) {
    final String tenantId = userService.currentTenantId();

    return eventTaskService.create(tenantId, measurement, scenario);
  }

  @GraphQLQuery
  public @GraphQLNonNull List<@GraphQLNonNull RetrievedEventEngineTask> eventTasks() {
    final String tenantId = userService.currentTenantId();

    return eventTaskService.getAll(tenantId).stream()
        .map(dto -> new RetrievedEventEngineTask()
            .setId(dto.getId())
            .setMeasurement(dto.getMeasurement())
            .setScenario(convertScenario(dto.getScenario()))
            .setScenarioType(dto.getScenario().getClass().getSimpleName())
        )
        .collect(Collectors.toList());
  }

  private EventTaskScenario convertScenario(Scenario scenario) {
    final EventTaskScenario eventTaskScenario = new EventTaskScenario();

    // I couldn't get Scenario so work as-is to be a GraphQL union since the abstract Scenario
    // class itself has no fields and results in this errors from SPQR:
    // "fields must be an object with field names as keys or a function which returns such an object"
    if (scenario instanceof Rising) {
      eventTaskScenario.setRising(((Rising) scenario));
    } else if (scenario instanceof Falling) {
      eventTaskScenario.setFalling(((Falling) scenario));
    }
    return eventTaskScenario;
  }

  @GraphQLMutation
  public DeleteResult deleteEventTask(@GraphQLNonNull String id) {
    final String tenantId = userService.currentTenantId();

    eventTaskService.delete(tenantId, id);

    return new DeleteResult().setSuccess(true);
  }
}
