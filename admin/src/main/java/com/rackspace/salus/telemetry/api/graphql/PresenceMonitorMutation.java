package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.model.SuccessResult;
import com.rackspace.salus.telemetry.etcd.services.WorkAllocationPartitionService;
import com.rackspace.salus.telemetry.etcd.types.WorkAllocationRealm;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PresenceMonitorMutation implements GraphQLMutationResolver {

  private final WorkAllocationPartitionService workAllocationPartitionService;

  @Autowired
  public PresenceMonitorMutation(WorkAllocationPartitionService workAllocationPartitionService) {
    this.workAllocationPartitionService = workAllocationPartitionService;
  }

  public CompletableFuture<SuccessResult> changePresenceMonitorPartitions(int count) {
    return workAllocationPartitionService.changePartitions(WorkAllocationRealm.PRESENCE_MONITOR, count)
        .thenApply(result -> new SuccessResult().setSuccess(result));
  }
}
