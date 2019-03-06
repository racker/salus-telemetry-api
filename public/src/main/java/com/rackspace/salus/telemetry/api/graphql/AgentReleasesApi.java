package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.AgentType;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class AgentReleasesApi {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AgentReleasesApi(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  //  agentReleases(id:String, type:AgentType): [AgentRelease!]!
  @GraphQLQuery
  public CompletableFuture<List<AgentRelease>> agentReleases(String id, AgentType agentType) {
    return agentsCatalogService.queryAgentReleases(id, agentType);
  }
}
