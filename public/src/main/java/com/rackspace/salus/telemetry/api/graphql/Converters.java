package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.AgentConfigInput;
import com.rackspace.salus.telemetry.api.model.AgentConfigResponse;
import com.rackspace.salus.telemetry.api.model.Label;
import com.rackspace.salus.telemetry.model.AgentConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converters {

  static List<Label> convertToLabelList(Map<String, String> labels) {
    return labels.entrySet().stream()
        .map(entry -> new Label()
            .setName(entry.getKey())
            .setValue(entry.getValue()))
        .collect(Collectors.toList());
  }

  static Map<String, String> convertToLabelMap(List<Label> labels) {
    final Map<String, String> converted = new HashMap<>();

    for (Label label : labels) {
      converted.put(label.getName(), label.getValue());
    }

    return converted;
  }

  static AgentConfigResponse convertToResponse(AgentConfig agentConfig) {
    return new AgentConfigResponse()
        .setId(agentConfig.getId())
        .setAgentType(agentConfig.getAgentType())
        .setContent(agentConfig.getContent())
        .setSelectorScope(agentConfig.getSelectorScope())
        .setLabels(convertToLabelList(agentConfig.getLabels()));
  }

  static List<AgentConfigResponse> convertToResponse(List<AgentConfig> agentConfigs) {
    return agentConfigs.stream()
        .map(Converters::convertToResponse
        )
        .collect(Collectors.toList());
  }

  static AgentConfig convertFromInput(AgentConfigInput config) {
    return new AgentConfig()
        .setAgentType(config.getAgentType())
        .setContent(config.getContent())
        .setSelectorScope(config.getSelectorScope())
        .setLabels(convertToLabelMap(config.getLabels()));
  }
}
