package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.Meters;
import com.rackspace.salus.telemetry.api.model.AgentConfigInput;
import com.rackspace.salus.telemetry.api.model.AgentConfigResponse;
import com.rackspace.salus.telemetry.api.model.DeleteResult;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.ConfigService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigMutation implements GraphQLMutationResolver {

  private final UserService userService;
  private final ConfigService configService;
  private final Counter creates;
  private final Counter modifies;
  private final Counter deletes;

  @Autowired
  public ConfigMutation(
      UserService userService, ConfigService configService, MeterRegistry meterRegistry) {
    this.userService = userService;
    this.configService = configService;

    creates = meterRegistry.counter("creates", "type", Meters.AGENT_CONFIGS_TYPE);
    modifies = meterRegistry.counter("modifies", "type", Meters.AGENT_CONFIGS_TYPE);
    deletes = meterRegistry.counter("deletes", "type", Meters.AGENT_CONFIGS_TYPE);
  }

  public CompletableFuture<AgentConfigResponse> createAgentConfig(AgentConfigInput config) {
    final String tenantId = userService.currentTenantId();

    log.debug("Creating agentConfig for tenant={}", tenantId);
    creates.increment();

    return configService.create(tenantId, Converters.convertFromInput(config))
        .thenApply(Converters::convertToResponse);
  }

  public CompletableFuture<AgentConfigResponse> modifyAgentConfig(String id, AgentConfigInput config) {
    final String tenantId = userService.currentTenantId();

    log.debug("Modifying agentConfig={} for tenant={}", id, tenantId);
    modifies.increment();

    return configService.modify(tenantId, Converters.convertFromInput(config), id)
        .thenApply(Converters::convertToResponse);
  }

  public CompletableFuture<DeleteResult> deleteAgentConfig(String id) {
    final String tenantId = userService.currentTenantId();

    log.debug("Deleting agentConfig={} for tenant={}", id, tenantId);
    deletes.increment();

    return configService.delete(tenantId, id)
        .thenApply(agentConfig -> new DeleteResult().setSuccess(agentConfig != null));
  }
}
