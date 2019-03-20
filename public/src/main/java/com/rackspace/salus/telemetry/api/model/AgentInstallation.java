package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.Label;
import java.util.List;
import lombok.Data;

@Data
public class AgentInstallation {
  String id;

  String agentReleaseId;

  List<Label> matchingLabels;
}
