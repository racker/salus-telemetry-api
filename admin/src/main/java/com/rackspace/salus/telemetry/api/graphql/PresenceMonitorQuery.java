package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.etcd.services.WorkAllocationPartitionService;
import com.rackspace.salus.telemetry.etcd.types.KeyRange;
import com.rackspace.salus.telemetry.etcd.types.WorkAllocationRealm;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PresenceMonitorQuery implements GraphQLQueryResolver {

  private final WorkAllocationPartitionService workAllocationPartitionService;

  @Autowired
  public PresenceMonitorQuery(WorkAllocationPartitionService workAllocationPartitionService) {
    this.workAllocationPartitionService = workAllocationPartitionService;
  }

  public CompletableFuture<List<KeyRange>> presenceMonitorPartitions() {
    return workAllocationPartitionService.getPartitions(WorkAllocationRealm.PRESENCE_MONITOR);
  }
}
