package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.rackspace.salus.telemetry.api.model.DeleteResponse;
import com.rackspace.salus.telemetry.api.services.DiagnosticsService;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeysMutation implements GraphQLMutationResolver {

  private final DiagnosticsService diagnosticsService;

  @Autowired
  public KeysMutation(DiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

  public CompletableFuture<DeleteResponse> deleteKey(String name) {
    return diagnosticsService.deleteKey(name)
        .thenApply(DeleteResponse::new);
  }

  public CompletableFuture<DeleteResponse> deleteKeys(String prefix) {
    return diagnosticsService.deleteKeysByPrefix(prefix)
        .thenApply(DeleteResponse::new);
  }

}
