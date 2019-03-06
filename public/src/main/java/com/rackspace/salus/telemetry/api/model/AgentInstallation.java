package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.Label;
import io.leangen.graphql.annotations.GraphQLIgnore;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AgentInstallation {
  String id;

  // this will be used to stitch together the final GraphQL response object
  @Getter(onMethod = @__({@GraphQLIgnore}))
  @Setter(onMethod = @__({@GraphQLIgnore}))
  String agentReleaseId;

  List<Label> matchingLabels;
}
