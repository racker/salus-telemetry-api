package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.model.Label;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.etcd.types.AgentInstallSelector;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@GraphQLApi
public class AgentInstallMutation {

  private final AgentsCatalogService agentsCatalogService;
  private final UserService userService;
  private final Counter agentInstalls;
  private final Counter agentUninstalls;

  @Autowired
  public AgentInstallMutation(AgentsCatalogService agentsCatalogService, UserService userService,
      MeterRegistry meterRegistry) {
    this.agentsCatalogService = agentsCatalogService;
    this.userService = userService;

    agentInstalls = meterRegistry.counter("agentInstalls");
    agentUninstalls = meterRegistry.counter("agentUninstalls");
  }
  @GraphQLMutation
  public CompletableFuture<AgentInstallation> installAgentRelease(String agentReleaseId,
      List<Label> labels) {
    final String tenantId = userService.currentTenantId();

    log.debug("Installing agent release={} for tenant={}", agentReleaseId, tenantId);
    agentInstalls.increment();

    return agentsCatalogService.install(
        tenantId,
        new AgentInstallSelector()
            .setAgentReleaseId(agentReleaseId)
            .setLabels(Converters.convertToLabelMap(labels))
    )
        .thenApply(result ->
            new AgentInstallation()
                .setId(result.getId())
                .setAgentReleaseId(result.getAgentReleaseId())
                .setLabels(Converters.convertToLabelList(result.getLabels()))
        );
  }
}
