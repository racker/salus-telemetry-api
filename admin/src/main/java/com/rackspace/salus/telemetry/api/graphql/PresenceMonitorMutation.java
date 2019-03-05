package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.etcd.services.WorkAllocationPartitionService;
import com.rackspace.salus.telemetry.etcd.types.WorkAllocationRealm;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class PresenceMonitorMutation {

  private final WorkAllocationPartitionService workAllocationPartitionService;

  @Autowired
  public PresenceMonitorMutation(WorkAllocationPartitionService workAllocationPartitionService) {
    this.workAllocationPartitionService = workAllocationPartitionService;
  }

  @GraphQLMutation
  public CompletableFuture<SuccessResult> changePresenceMonitorPartitions(int count) {
    return workAllocationPartitionService.changePartitions(WorkAllocationRealm.PRESENCE_MONITOR, count)
        .thenApply(result -> new SuccessResult().setSuccess(result));
  }
}
