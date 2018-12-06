package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.model.NotFoundException;
import graphql.GraphQLException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GraphQL will scan for {@link ExceptionHandler} annotated methods that return a {@link GraphQLException}
 * in order to translate application specific annotations, matched by the method argument type,
 * into one that GraphQL can specifically convey back to the caller of the API.
 */
@Component
public class ExceptionHandlers {

  @ExceptionHandler
  public GraphQLException handleNotFound(NotFoundException e) {
    // GraphQL will extract things like the message from the given exception, so it might turn
    // out this method can be broadened to catch any type of exception.
    return new GraphQLException("Not found", e);
  }

}
