package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class AgentInstallQuery {

  private final AgentsCatalogService agentsCatalogService;
  private final UserService userService;

  @Autowired
  public AgentInstallQuery(AgentsCatalogService agentsCatalogService, UserService userService) {
    this.agentsCatalogService = agentsCatalogService;
    this.userService = userService;
  }

  @GraphQLQuery
  public CompletableFuture<List<AgentInstallation>> agentInstallations() {
    final String tenantId = userService.currentTenantId();

    return agentsCatalogService.getInstallations(tenantId)
        .thenApply(results ->
            results.stream()
                .map(selector ->
                    new AgentInstallation()
                        .setId(selector.getId())
                        .setAgentReleaseId(selector.getAgentReleaseId())
                        .setLabels(Converters.convertToLabelList(selector.getLabels()))
                )
                .collect(Collectors.toList())
        );
  }
}
