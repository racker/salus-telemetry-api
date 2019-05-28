package com.rackspace.salus.telemetry.api.model;

import java.util.Map;
import lombok.Data;

@Data
public class AgentInstallation {
  String id;

  String agentReleaseId;

  Map<String,String> labelSelector;
}
