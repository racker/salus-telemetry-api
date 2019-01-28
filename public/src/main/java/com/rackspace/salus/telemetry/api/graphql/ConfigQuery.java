package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.Meters;
import com.rackspace.salus.telemetry.api.model.AgentConfigResponse;
import com.rackspace.salus.telemetry.api.services.UserService;
import com.rackspace.salus.telemetry.etcd.services.ConfigService;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@GraphQLApi
public class ConfigQuery {

  private final UserService userService;
  private final ConfigService configService;
  private final Counter agentConfigQueries;

  @Autowired
  public ConfigQuery(UserService userService, ConfigService configService, MeterRegistry meterRegistry) {
    this.userService = userService;
    this.configService = configService;

    agentConfigQueries = meterRegistry.counter("queries", "type", Meters.AGENT_CONFIGS_TYPE);
  }

  @GraphQLQuery
  public CompletableFuture<List<AgentConfigResponse>> agentConfigs(String id) {
    final String tenantId = userService.currentTenantId();

    log.debug("Querying agent configs for tenant={}. Criteria: id={}",
        tenantId, id);
    agentConfigQueries.increment();

    if (StringUtils.hasText(id)) {
      return configService.getOne(tenantId, id)
          .thenApply(Converters::convertToResponse);
    }
    else {
      return configService.getAll(tenantId)
          .thenApply(Converters::convertToResponse);
    }
  }

}
