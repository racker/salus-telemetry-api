package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.model.NotFoundException;
import graphql.GraphQLException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class ExceptionHandlers {

  @ExceptionHandler
  public GraphQLException handleNotFound(NotFoundException e) {
    return new GraphQLException("Not found", e);
  }

}
