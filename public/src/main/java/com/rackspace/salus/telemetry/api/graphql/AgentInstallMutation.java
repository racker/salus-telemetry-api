package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.model.AgentInstallation;
import com.rackspace.salus.telemetry.api.model.Label;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentInstallSelector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AgentInstallMutation implements GraphQLMutationResolver {

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

  public CompletableFuture<AgentInstallation> installAgentRelease(String agentReleaseId,
      List<Label> labels) {
    final String tenantId = userService.currentTenantId();

    log.debug("Installing agent release={} for tenant={}", agentReleaseId, tenantId);
    agentInstalls.increment();

    return agentsCatalogService.install(
        tenantId,
        new AgentInstallSelector()
            .setAgentInfoId(agentReleaseId)
            .setLabels(Converters.convertToLabelMap(labels))
    )
        .thenApply(result ->
            new AgentInstallation()
                .setId(result.getId())
                .setAgentReleaseId(result.getAgentInfoId())
                .setLabels(Converters.convertToLabelList(result.getLabels()))
        );
  }
}
