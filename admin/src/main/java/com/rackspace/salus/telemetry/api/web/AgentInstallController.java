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

import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.etcd.types.AgentInstallSelector;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentInstallController {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AgentInstallController(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  @GetMapping("/agent-installs/{tenantId}")
  public CompletableFuture<List<AgentInstallSelector>> getAll(@PathVariable String tenantId) {

    return agentsCatalogService.getInstallations(tenantId);
  }

  @PostMapping("/agent-installs/{tenantId}")
  public CompletableFuture<AgentInstallSelector> install(@PathVariable String tenantId,
                                                         @RequestBody @Valid AgentInstallSelector agentInstallSelector) {

    return agentsCatalogService.install(tenantId, agentInstallSelector);
  }
}
