package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.AgentType;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@GraphQLApi
public class AdminAgentCatalogQuery {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AdminAgentCatalogQuery(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  @GraphQLQuery
  public CompletableFuture<List<AgentRelease>> agentReleases(String id, AgentType type) {
    return agentsCatalogService.queryAgentReleases(id, type);
  }

  private CompletableFuture<List<AgentRelease>> queryAgentReleases(String id, AgentType type) {
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
