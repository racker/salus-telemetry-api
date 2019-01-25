package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.AgentType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class PublicAgentCatalogQuery {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public PublicAgentCatalogQuery(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  //  agentReleases(id:String, type:AgentType): [AgentRelease!]!
  @GraphQLQuery
  public CompletableFuture<List<AgentRelease>> agentReleases(String id, AgentType agentType) {
    return agentsCatalogService.queryAgentReleases(id, agentType);
  }
}
