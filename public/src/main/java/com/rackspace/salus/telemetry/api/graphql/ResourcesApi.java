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

import static com.rackspace.salus.telemetry.api.graphql.Converters.convertToLabelMap;

import com.rackspace.salus.telemetry.api.Meters;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.model.Resource;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.EnvoyResourceManagement;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.ResourceInfo;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@GraphQLApi
public class ResourcesApi {

    private final EnvoyResourceManagement envoyResourceManagement;
    private final UserService userService;
    private final Counter creates;
    private final Counter deletes;

    public ResourcesApi(EnvoyResourceManagement envoyResourceManagement, UserService userService, MeterRegistry meterRegistry) {
        this.envoyResourceManagement = envoyResourceManagement;
        this.userService = userService;

        creates = meterRegistry.counter("creates", "type", Meters.RESOURCES_TYPE);
        deletes = meterRegistry.counter("deletes", "type", Meters.RESOURCES_TYPE);
    }

    @GraphQLMutation
    public CompletableFuture<Resource> createResource(@GraphQLNonNull String resourceId,
                                                      @GraphQLNonNull List<Label> labels) {
        final String tenantId = userService.currentTenantId();

        log.debug("Creating new resource for tenant={}", tenantId);
        creates.increment();

        return envoyResourceManagement.create(tenantId, new ResourceInfo()
            .setResourceId(resourceId)
            .setLabels(convertToLabelMap(labels)))
                .thenApply(Converters::convertToResponse);
    }

    @GraphQLMutation
    public CompletableFuture<DeleteResult> deleteResource(@GraphQLNonNull String resourceId) {
        final String tenantId = userService.currentTenantId();

        log.debug("Deleting resource with resourceId={} for tenant={}",
                resourceId, tenantId);
        deletes.increment();

        return envoyResourceManagement.delete(tenantId, resourceId)
                .thenApply(deleteResponse -> new DeleteResult().setSuccess(deleteResponse != null));
    }

    @GraphQLQuery
    public CompletableFuture<List<Resource>> resources() {
        final String tenantId = userService.currentTenantId();

        log.debug("Querying resources for tenant={}", tenantId);

        return envoyResourceManagement.getAll(tenantId)
            .thenApply(Converters::convertToResourceResponse);
    }

    @GraphQLQuery
    public CompletableFuture<List<Resource>> resources(@GraphQLArgument(name = "resourceId") String resourceId) {
        final String tenantId = userService.currentTenantId();

        log.debug("Querying resources for tenant={}", tenantId);

        return envoyResourceManagement.getOne(tenantId, resourceId)
            .thenApply(Converters::convertToResourceResponse);
    }

}
