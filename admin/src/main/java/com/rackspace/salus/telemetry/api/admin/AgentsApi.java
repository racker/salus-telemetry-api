/*
 *    Copyright 2018 Rackspace US, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package com.rackspace.salus.telemetry.api.admin;

import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentInfo;
import com.rackspace.salus.telemetry.model.AgentType;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/agents")
public class AgentsApi {

    private final AgentsCatalogService agentsCatalogService;

    @Autowired
    public AgentsApi(AgentsCatalogService agentsCatalogService) {
        this.agentsCatalogService = agentsCatalogService;
    }

    @PostMapping
    public CompletableFuture<AgentInfo> declareAgentInfo(@RequestBody @Validated AgentInfo agentInfo) {

        return agentsCatalogService.declare(agentInfo);
    }

    @GetMapping("/byType/{agentType}")
    public CompletableFuture<List<AgentInfo>> getAgentsByType(@PathVariable AgentType agentType) {
        return agentsCatalogService.getAgentsByType(agentType);
    }

    @GetMapping("/byId/{agentId}")
    public CompletableFuture<AgentInfo> getAgentByType(@PathVariable String agentId) {
        return agentsCatalogService.getAgentById(agentId);
    }
}
