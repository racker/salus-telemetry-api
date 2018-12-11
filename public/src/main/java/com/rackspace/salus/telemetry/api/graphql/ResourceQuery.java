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

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.api.model.ResourceResponse;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.EnvoyResourceManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ResourceQuery implements GraphQLQueryResolver {

    private final EnvoyResourceManagement envoyResourceManagement;
    private final UserService userService;

    @Autowired
    public ResourceQuery(EnvoyResourceManagement envoyResourceManagement, UserService userService) {
        this.envoyResourceManagement = envoyResourceManagement;
        this.userService = userService;
    }

    public CompletableFuture<List<ResourceResponse>> resources(@Nullable String identifier, @Nullable String identifierValue) {
        final String tenantId = userService.currentTenantId();

        log.debug("Querying resources for tenant={}", tenantId);

        if (StringUtils.hasText(identifier) && StringUtils.hasText(identifierValue)) {
            log.debug("Getting resources with identifier value for tenant={}", tenantId);
            return envoyResourceManagement.getOne(tenantId, identifier, identifierValue)
                    .thenApply(Converters::convertToResourceResponse);
        } else if (StringUtils.hasText(identifier)) {
            log.debug("Getting resources with identifier for tenant={}", tenantId);
            return envoyResourceManagement.getSome(tenantId, identifier)
                    .thenApply(Converters::convertToResourceResponse);
        } else {
            log.debug("Getting all resources for tenant={}", tenantId);
            return envoyResourceManagement.getAll(tenantId)
                    .thenApply(Converters::convertToResourceResponse);
        }
    }

}
