package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.DeleteResponse;
import com.rackspace.salus.telemetry.api.services.DiagnosticsService;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeysMutation {

  private final DiagnosticsService diagnosticsService;

  @Autowired
  public KeysMutation(DiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

  @GraphQLQuery
  public CompletableFuture<DeleteResponse> deleteKey(String name) {
    return diagnosticsService.deleteKey(name)
        .thenApply(DeleteResponse::new);
  }

  @GraphQLQuery
  public CompletableFuture<DeleteResponse> deleteKeys(String prefix) {
    return diagnosticsService.deleteKeysByPrefix(prefix)
        .thenApply(DeleteResponse::new);
  }

}
