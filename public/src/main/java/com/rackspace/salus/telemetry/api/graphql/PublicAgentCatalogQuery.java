package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.AgentType;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicAgentCatalogQuery implements GraphQLQueryResolver {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public PublicAgentCatalogQuery(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  //  agentReleases(id:String, type:AgentType): [AgentRelease!]!
  public CompletableFuture<List<AgentRelease>> agentReleases(String id, AgentType agentType) {
    return agentsCatalogService.queryAgentReleases(id, agentType);
  }
}
