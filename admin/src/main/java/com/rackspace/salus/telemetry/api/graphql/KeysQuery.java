package com.rackspace.salus.telemetry.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.rackspace.salus.telemetry.api.model.KVEntry;
import com.rackspace.salus.telemetry.api.services.DiagnosticsService;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KeysQuery implements GraphQLQueryResolver {

  private final DiagnosticsService diagnosticsService;

  @Autowired
  public KeysQuery(DiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

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
