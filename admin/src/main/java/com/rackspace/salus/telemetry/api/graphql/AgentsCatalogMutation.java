package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.config.ApiAdminProperties;
import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.api.model.input.AgentRelease;
import com.rackspace.salus.telemetry.etcd.services.AgentsCatalogService;
import com.rackspace.salus.telemetry.model.Checksum;
import com.rackspace.salus.telemetry.model.Label;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class AgentsCatalogMutation {

  private final AgentsCatalogService agentsCatalogService;
  private final ApiAdminProperties properties;

  @Autowired
  public AgentsCatalogMutation(AgentsCatalogService agentsCatalogService, ApiAdminProperties properties) {
    this.agentsCatalogService = agentsCatalogService;
    this.properties = properties;
  }

  @GraphQLMutation
  public CompletableFuture<com.rackspace.salus.telemetry.model.AgentRelease> declareAgentRelease(AgentRelease input) {
    if (!properties.getRequiredAgentLabels().stream()
        .allMatch(s -> containsLabel(input, s))) {
      throw new IllegalArgumentException(
          "AgentRelease is missing one or more required labels: " + properties.getRequiredAgentLabels()
      );
    };

    final com.rackspace.salus.telemetry.model.AgentRelease agentRelease = new com.rackspace.salus.telemetry.model.AgentRelease()
        .setType(input.getType())
        .setUrl(input.getUrl())
        .setLabels(Label.convertToMap(input.getLabelSelector()))
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
    if (input.getLabelSelector() == null) {
      return false;
    }

    for (Label label : input.getLabelSelector()) {
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
