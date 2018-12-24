/*
 * Copyright 2018 Rackspace US, Inc.
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

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.Meters;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.model.ResourceInput;
import com.rackspace.salus.telemetry.api.model.ResourceResponse;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.EnvoyResourceManagement;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ResourceMutation implements GraphQLMutationResolver {

    private final EnvoyResourceManagement envoyResourceManagement;
    private final UserService userService;
    private final Counter creates;
    private final Counter deletes;

    public ResourceMutation(EnvoyResourceManagement envoyResourceManagement, UserService userService, MeterRegistry meterRegistry) {
        this.envoyResourceManagement = envoyResourceManagement;
        this.userService = userService;

        creates = meterRegistry.counter("creates", "type", Meters.RESOURCES_TYPE);
        deletes = meterRegistry.counter("deletes", "type", Meters.RESOURCES_TYPE);
    }

    public CompletableFuture<ResourceResponse> createResource(ResourceInput resource) {
        final String tenantId = userService.currentTenantId();

        log.debug("Creating new resource for tenant={}", tenantId);
        creates.increment();

        return envoyResourceManagement.create(tenantId, Converters.convertResourceFromInput(resource))
                .thenApply(Converters::convertToResponse);
    }

    public CompletableFuture<DeleteResult> deleteResource(String identifierName, String identifierValue) {
        final String tenantId = userService.currentTenantId();

        log.debug("Deleting resource with identifierName={} identifierValue={} for tenant={}",
                identifierName, identifierValue, tenantId);
        deletes.increment();

        return envoyResourceManagement.delete(tenantId, identifierName, identifierValue)
                .thenApply(deleteResponse -> new DeleteResult().setSuccess(deleteResponse != null));
    }

}
