package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.input.AgentRelease;
import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.Checksum;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class AgentsCatalogMutation {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AgentsCatalogMutation(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  @GraphQLMutation
  public CompletableFuture<com.rackspace.salus.telemetry.model.AgentRelease> declareAgentRelease(AgentRelease input) {
    final com.rackspace.salus.telemetry.model.AgentRelease agentRelease = new com.rackspace.salus.telemetry.model.AgentRelease()
        .setType(input.getType())
        .setUrl(input.getUrl())
        .setArch(input.getArch())
        .setOs(input.getOs())
        .setVersion(input.getVersion())
        .setExe(input.getExe())
        ;

    if (input.getChecksum() != null) {
      agentRelease.setChecksum(
          new Checksum()
          .setType(input.getChecksum().getType())
          .setValue(input.getChecksum().getValue())
      );
    }

    return agentsCatalogService.declare(agentRelease);
  }

  public CompletableFuture<SuccessResult> deleteAgentRelease(String id) {
    return agentsCatalogService.deleteAgentRelease(id)
        .thenApply(success -> new SuccessResult().setSuccess(success));
  }
}
