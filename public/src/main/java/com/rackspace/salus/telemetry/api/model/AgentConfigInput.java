package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.ConfigSelectorScope;
import java.util.List;
import lombok.Data;

@Data
public class AgentConfigInput {
  AgentType agentType;

  String content;

  ConfigSelectorScope selectorScope;

  List<Label> labels;
}
