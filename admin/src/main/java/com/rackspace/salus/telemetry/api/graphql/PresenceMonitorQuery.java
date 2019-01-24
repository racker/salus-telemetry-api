package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.etcd.services.WorkAllocationPartitionService;
import com.rackspace.salus.telemetry.etcd.types.KeyRange;
import com.rackspace.salus.telemetry.etcd.types.WorkAllocationRealm;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotation.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class PresenceMonitorQuery {

  private final WorkAllocationPartitionService workAllocationPartitionService;

  @Autowired
  public PresenceMonitorQuery(WorkAllocationPartitionService workAllocationPartitionService) {
    this.workAllocationPartitionService = workAllocationPartitionService;
  }

  @GraphQLQuery
  public CompletableFuture<List<KeyRange>> presenceMonitorPartitions() {
    return workAllocationPartitionService.getPartitions(WorkAllocationRealm.PRESENCE_MONITOR);
  }
}
