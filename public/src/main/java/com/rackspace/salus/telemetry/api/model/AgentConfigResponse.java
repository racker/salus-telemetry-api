package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.ConfigSelectorScope;
import java.util.List;
import lombok.Data;

@Data
public class AgentConfigResponse {
  String id;

  AgentType agentType;

  String content;

  ConfigSelectorScope selectorScope;

  List<Label> labels;
}
