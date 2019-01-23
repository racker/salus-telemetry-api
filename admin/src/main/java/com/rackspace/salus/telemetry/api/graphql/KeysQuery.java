package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.KVEntry;
import com.rackspace.salus.telemetry.api.services.DiagnosticsService;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KeysQuery {

  private final DiagnosticsService diagnosticsService;

  @Autowired
  public KeysQuery(DiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

  @GraphQLQuery
  public CompletableFuture<List<KVEntry>> keys(String name, String prefix) {
    if (StringUtils.hasText(name)) {
      return diagnosticsService.getKey(name)
          .thenApply(Collections::singletonList);
    }
    else {
      return diagnosticsService.getKeys(prefix);
    }
  }
}
