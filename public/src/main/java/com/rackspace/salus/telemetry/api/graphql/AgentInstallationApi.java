package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.etcd.types.AgentInstallSelector;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.Label;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@GraphQLApi
public class AgentInstallationApi {

  private final AgentsCatalogService agentsCatalogService;
  private final UserService userService;
  private final Counter agentInstalls;
  private final Counter agentUninstalls;

  @Autowired
  public AgentInstallationApi(AgentsCatalogService agentsCatalogService, UserService userService,
                              MeterRegistry meterRegistry) {
    this.agentsCatalogService = agentsCatalogService;
    this.userService = userService;

    agentInstalls = meterRegistry.counter("agentInstalls");
    agentUninstalls = meterRegistry.counter("agentUninstalls");
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
                        .setMatchingLabels(Label.convertToLabelList(selector.getLabels()))
                )
                .collect(Collectors.toList())
        );
  }

  @GraphQLQuery
  public CompletableFuture<AgentRelease> agentRelease(@GraphQLContext AgentInstallation agentInstallation) {
    //IMPLEMENT ME
    return null;
  }

  @GraphQLMutation
  public CompletableFuture<AgentInstallation> installAgentRelease(@GraphQLNonNull String agentReleaseId,
      List<Label> matchingLabels) {
    final String tenantId = userService.currentTenantId();

    log.debug("Installing agent release={} for tenant={}", agentReleaseId, tenantId);
    agentInstalls.increment();

    return agentsCatalogService.install(
        tenantId,
        new AgentInstallSelector()
            .setAgentReleaseId(agentReleaseId)
            .setLabels(Label.convertToMap(matchingLabels))
    )
        .thenApply(result ->
            new AgentInstallation()
                .setId(result.getId())
                .setAgentReleaseId(result.getAgentReleaseId())
                .setMatchingLabels(Label.convertToLabelList(result.getLabels()))
        );
  }
}
