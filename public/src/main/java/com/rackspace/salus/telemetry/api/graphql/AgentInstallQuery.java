package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentInstallQuery implements GraphQLQueryResolver {

  private final AgentsCatalogService agentsCatalogService;
  private final UserService userService;

  @Autowired
  public AgentInstallQuery(AgentsCatalogService agentsCatalogService, UserService userService) {
    this.agentsCatalogService = agentsCatalogService;
    this.userService = userService;
  }

  public CompletableFuture<List<AgentInstallation>> agentInstallations() {
    final String tenantId = userService.currentTenantId();

    return agentsCatalogService.getInstallations(tenantId)
        .thenApply(results ->
            results.stream()
                .map(selector ->
                    new AgentInstallation()
                        .setId(selector.getId())
                        .setAgentReleaseId(selector.getAgentInfoId())
                        .setLabels(Converters.convertToLabelList(selector.getLabels()))
                )
                .collect(Collectors.toList())
        );
  }
}
