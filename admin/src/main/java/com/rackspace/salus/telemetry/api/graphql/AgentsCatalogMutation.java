package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.model.AgentReleaseInput;
import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.AgentRelease;
import com.rackspace.salus.telemetry.model.Checksum;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentsCatalogMutation implements GraphQLMutationResolver {

  private final AgentsCatalogService agentsCatalogService;

  @Autowired
  public AgentsCatalogMutation(AgentsCatalogService agentsCatalogService) {
    this.agentsCatalogService = agentsCatalogService;
  }

  public CompletableFuture<AgentRelease> declareAgentRelease(AgentReleaseInput input) {
    final AgentRelease agentRelease = new AgentRelease()
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
