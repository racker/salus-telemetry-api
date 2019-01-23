package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.etcd.services.WorkAllocationPartitionService;
import com.rackspace.salus.telemetry.etcd.types.WorkAllocationRealm;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PresenceMonitorMutation {

  private final WorkAllocationPartitionService workAllocationPartitionService;

  @Autowired
  public PresenceMonitorMutation(WorkAllocationPartitionService workAllocationPartitionService) {
    this.workAllocationPartitionService = workAllocationPartitionService;
  }

  @GraphQLQuery
  public CompletableFuture<SuccessResult> changePresenceMonitorPartitions(int count) {
    return workAllocationPartitionService.changePartitions(WorkAllocationRealm.PRESENCE_MONITOR, count)
        .thenApply(result -> new SuccessResult().setSuccess(result));
  }
}
