package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.api.model.input.AgentRelease;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.Architecture;
import com.rackspace.salus.telemetry.model.Checksum;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.OperatingSystem;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    Assert.isTrue(containsLabel(input, OperatingSystem.NAME), "Missing required label: "+OperatingSystem.NAME);
    Assert.isTrue(containsLabel(input, Architecture.NAME), "Missing required label: "+Architecture.NAME);

    final com.rackspace.salus.telemetry.model.AgentRelease agentRelease = new com.rackspace.salus.telemetry.model.AgentRelease()
        .setType(input.getType())
        .setUrl(input.getUrl())
        .setLabels(Label.convertToMap(input.getLabels()))
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

  private static boolean containsLabel(AgentRelease input, String name) {
    if (input.getLabels() == null) {
      return false;
    }

    for (Label label : input.getLabels()) {
      if (label.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public CompletableFuture<SuccessResult> deleteAgentRelease(String id) {
    return agentsCatalogService.deleteAgentRelease(id)
        .thenApply(success -> new SuccessResult().setSuccess(success));
  }
}
