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

import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.model.InstallAgent;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.etcd.types.AgentInstallSelector;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * NOTE: this controller's API will soon be moved to the internal-admin API to enable support
 * personnel to manage deployment of agent releases for their customers.
 */
@RestController
@RequestMapping("/api/agentInstalls")
@Slf4j
public class AgentInstallationController {

  private final AgentsCatalogService agentsCatalogService;
  private final UserService userService;

  @Autowired
  public AgentInstallationController(AgentsCatalogService agentsCatalogService, UserService userService) {
    this.agentsCatalogService = agentsCatalogService;
    this.userService = userService;
  }

  @GetMapping
  public CompletableFuture<List<AgentInstallation>> getAllAgentInstallations() {
    final String tenantId = userService.currentTenantId();

    return agentsCatalogService.getInstallations(tenantId)
        .thenApply(results ->
            results.stream()
                .map(selector ->
                    new AgentInstallation()
                        .setId(selector.getId())
                        .setAgentReleaseId(selector.getAgentReleaseId())
                        .setLabelSelector(selector.getLabels())
                )
                .collect(Collectors.toList())
        );
  }

  @PostMapping
  public CompletableFuture<AgentInstallation> installAgentRelease(
      @RequestBody @Valid InstallAgent install) {
    final String tenantId = userService.currentTenantId();

    final String agentReleaseId = install.getAgentReleaseId();

    log.debug("Installing agent release={} for tenant={}", agentReleaseId, tenantId);

    return agentsCatalogService.install(
        tenantId,
        new AgentInstallSelector()
            .setAgentReleaseId(agentReleaseId)
            .setLabels(install.getLabelSelector())
    )
        .thenApply(result ->
            new AgentInstallation()
                .setId(result.getId())
                .setAgentReleaseId(result.getAgentReleaseId())
                .setLabelSelector(result.getLabels())
        );
  }
}
