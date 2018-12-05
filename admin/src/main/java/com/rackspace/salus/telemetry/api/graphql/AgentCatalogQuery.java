package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.AgentType;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AgentCatalogQuery implements GraphQLQueryResolver {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AgentCatalogQuery(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  public CompletableFuture<List<AgentRelease>> agentReleases(String id, AgentType type) {
    if (StringUtils.hasText(id)) {
      return agentsCatalogService.getAgentById(id)
          .thenApply(Collections::singletonList);
    }
    else if (type != null) {
      return agentsCatalogService.getAgentReleasesByType(type);
    }
    else {
      return agentsCatalogService.getAllAgentReleases();
    }
  }
}
