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

import com.rackspace.salus.telemetry.api.Meters;
import com.rackspace.salus.telemetry.api.model.CreatedResource;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.services.FrontendResourceService;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.PagedContent;
import com.rackspace.salus.telemetry.model.Resource;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@GraphQLApi
public class ResourcesApi {

  private final UserService userService;
  private final Counter creates;
  private final Counter deletes;
  private final FrontendResourceService resourceService;

  public ResourcesApi(UserService userService,
                      MeterRegistry meterRegistry,
                      FrontendResourceService resourceService) {
    this.userService = userService;

    creates = meterRegistry.counter("creates", "type", Meters.RESOURCES_TYPE);
    deletes = meterRegistry.counter("deletes", "type", Meters.RESOURCES_TYPE);
    this.resourceService = resourceService;
  }

  @GraphQLMutation
  public CreatedResource createResource(@GraphQLNonNull String resourceId,
                                        @GraphQLNonNull List<Label> labels) {
    final String tenantId = userService.currentTenantId();

    log.debug("Creating new resource for tenant={}", tenantId);
    creates.increment();

    return resourceService.create(tenantId, resourceId, labels);
  }

  @GraphQLMutation
  public DeleteResult deleteResource(@GraphQLNonNull String resourceId) {
    final String tenantId = userService.currentTenantId();

    log.debug("Deleting resource with resourceId={} for tenant={}",
        resourceId, tenantId
    );
    deletes.increment();

    resourceService.delete(tenantId, resourceId);

    return new DeleteResult().setSuccess(true);
  }

  @GraphQLQuery
  public PagedContent<Resource> resources(
      @GraphQLArgument(name = "size", defaultValue = "100") int size,
      @GraphQLArgument(name = "page", defaultValue = "0") int page) {
    final String tenantId = userService.currentTenantId();

    log.debug("Querying resources for tenant={}", tenantId);

    return resourceService.getAll(tenantId, size, page);
  }

  @GraphQLQuery
  public PagedContent<Resource> resources(@GraphQLArgument(name = "resourceId") String resourceId,
                                          @GraphQLArgument(name = "size", defaultValue = "100") int size,
                                          @GraphQLArgument(name = "page", defaultValue = "0") int page) {
    // NOTE: SPQR wants size and page to be present when using resourceId argument

    final String tenantId = userService.currentTenantId();

    log.debug("Querying resources for tenant={}", tenantId);

    return PagedContent.ofSingleton(
        resourceService.getOne(tenantId, resourceId)
    );
  }

}
