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

import com.rackspace.salus.telemetry.api.model.CreatedMonitor;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.model.LocalMonitorConfigs;
import com.rackspace.salus.telemetry.api.model.ModifyResult;
import com.rackspace.salus.telemetry.api.model.RemoteMonitorConfigs;
import com.rackspace.salus.telemetry.api.model.RetrievedMonitor;
import com.rackspace.salus.telemetry.api.model.telegraf.UsingLocalTelegraf;
import com.rackspace.salus.telemetry.api.services.FrontendMonitorService;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.PagedContent;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@GraphQLApi
@Service
public class MonitorApi {

  private final UserService userService;
  private final FrontendMonitorService monitorService;

  @Autowired
  public MonitorApi(UserService userService, FrontendMonitorService monitorService) {
    this.userService = userService;
    this.monitorService = monitorService;
  }

  @GraphQLMutation
  public CreatedMonitor createLocalMonitor(@GraphQLNonNull LocalMonitorConfigs configs,
                                           @GraphQLNonNull List<@GraphQLNonNull Label> matchingLabels) {
    final String tenantId = userService.currentTenantId();

    return monitorService.createMonitor(tenantId, configs, matchingLabels);
  }

  @GraphQLMutation
  public CreatedMonitor createRemoteMonitor(@GraphQLNonNull RemoteMonitorConfigs configs,
                                            @GraphQLNonNull String targetResourceId,
                                            @GraphQLNonNull List<@GraphQLNonNull Label> matchingLabels) {
    //TODO this is a placeholder for GraphQL schema declaration
    return null;
  }

  @GraphQLMutation
  public ModifyResult modifyLocalMonitor(@GraphQLNonNull String id,
                                         @GraphQLNonNull LocalMonitorConfigs configs) {
    //TODO this is a placeholder for GraphQL schema declaration
    return null;
  }

  @GraphQLMutation
  public ModifyResult modifyRemoteMonitor(@GraphQLNonNull String id,
                                          @GraphQLNonNull RemoteMonitorConfigs configs) {
    //TODO this is a placeholder for GraphQL schema declaration
    return null;
  }

  @GraphQLMutation
  public DeleteResult deleteMonitor(@GraphQLNonNull String id) {
    final String tenantId = userService.currentTenantId();

    monitorService.delete(tenantId, id);

    return new DeleteResult().setSuccess(true);
  }

  @GraphQLQuery
  public PagedContent<RetrievedMonitor> monitors(
      @GraphQLArgument(name = "size", defaultValue = "100") int size,
      @GraphQLArgument(name = "page", defaultValue = "0") int page) {
    final String tenantId = userService.currentTenantId();
    return monitorService.retrieve(tenantId, size, page);
  }

  @GraphQLQuery
  public String configJson(@GraphQLContext UsingLocalTelegraf usingLocalTelegraf) {
    return monitorService.convertToJson(usingLocalTelegraf);
  }

  @GraphQLQuery
  public List<String> enabled(@GraphQLContext UsingLocalTelegraf usingLocalTelegraf) {
    return usingLocalTelegraf.buildEnabledList();
  }

}
