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

package com.rackspace.salus.telemetry.api.tenant;

import com.rackspace.salus.telemetry.etcd.services.ConfigService;
import com.rackspace.salus.telemetry.model.AgentConfig;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{tenantId}/configs")
public class TenantConfigApi {

    private final ConfigService configService;

    @Autowired
    public TenantConfigApi(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping
    public CompletableFuture<AgentConfig> createConfig(@PathVariable String tenantId,
                                                       @RequestBody @Validated AgentConfig agentConfig) {
        return configService.create(tenantId, agentConfig);
    }

    @GetMapping
    public CompletableFuture<List<AgentConfig>> getConfigs(@PathVariable String tenantId) {
        return configService.get(tenantId);
    }

    @PutMapping("/{id}")
    public CompletableFuture<AgentConfig> modifyConfig(@PathVariable String tenantId,
                                                       @RequestBody @Validated AgentConfig agentConfig,
                                                       @PathVariable String id) {
        return configService.modify(tenantId, agentConfig, id);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<AgentConfig> deleteConfig(@PathVariable String tenantId,
                                                       @PathVariable String id) {
        return configService.delete(tenantId, id);
    }
}
