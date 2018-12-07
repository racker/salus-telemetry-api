package com.rackspace.salus.telemetry.api.graphql;

import graphql.GraphQLException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class ExceptionHandlers {

  @ExceptionHandler
  public GraphQLException handleIllegalArgument(IllegalArgumentException e) {
    return new GraphQLException("Illegal argument", e);
  }
}
