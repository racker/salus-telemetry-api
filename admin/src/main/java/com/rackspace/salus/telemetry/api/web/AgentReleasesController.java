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

import com.rackspace.salus.telemetry.api.config.ApiAdminProperties;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.NotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentReleasesController {

  private final AgentsCatalogService agentsCatalogService;
  private final ApiAdminProperties apiAdminProperties;

  @Autowired
  public AgentReleasesController(AgentsCatalogService agentsCatalogService,
                                 ApiAdminProperties apiAdminProperties) {

    this.agentsCatalogService = agentsCatalogService;
    this.apiAdminProperties = apiAdminProperties;
  }

  @GetMapping("/agent-releases")
  public CompletableFuture<List<AgentRelease>> getAll() {
    return agentsCatalogService.queryAgentReleases(null, null);
  }

  @GetMapping("/agent-releases/{id}")
  public CompletableFuture<AgentRelease> getOne(@PathVariable String id) {
    return agentsCatalogService.queryAgentReleases(id, null)
        .thenApply(agentReleases -> {
          // NOTE queryAgentReleases will normally be the one that throws NotFound, but
          // we'll handle an emtpy response also, just to be safe
          if (agentReleases.isEmpty()) {
            throw new NotFoundException("Unable to find agent release by ID");
          }

          return agentReleases.get(0);
        });
  }

  @PostMapping("/agent-releases")
  public CompletableFuture<AgentRelease> declareAgentRelease(
      @RequestBody @Valid AgentRelease agentRelease) {

    // quick and simple validation of required agent release labels
    if (!apiAdminProperties.getRequiredAgentLabels().stream()
        .allMatch(s -> agentRelease.getLabels().containsKey(s))) {
      throw new IllegalArgumentException(
          "AgentRelease is missing one or more required labels: " +
              apiAdminProperties.getRequiredAgentLabels()
      );
    };

    return agentsCatalogService.declare(agentRelease);
  }

  @DeleteMapping("/agent-releases/{id}")
  public CompletableFuture<Void> deleteOne(@PathVariable String id) {
    return agentsCatalogService.deleteAgentRelease(id)
        .thenApply(exists -> {
          if (exists) {
            return null;
          }
          else {
            throw new NotFoundException("Unable to find agent release by ID");
          }
        });
  }
}
