package com.rackspace.salus.telemetry.api.graphql;

import com.rackspace.salus.telemetry.api.model.Resource;
import com.rackspace.salus.telemetry.model.Label;
import com.rackspace.salus.telemetry.model.ResourceInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  static Resource convertToResponse(ResourceInfo resourceInfo) {
    Resource response = new Resource()
            .setResourceId(resourceInfo.getResourceId())
            .setTenantId(resourceInfo.getTenantId())
            .setEnvoyId(resourceInfo.getEnvoyId())
            .setLabels(convertToLabelList(resourceInfo.getLabels()));

    if (resourceInfo.getAddress() != null) {
      response.setAddress(resourceInfo.getAddress().toString());
    }
    return response;
  }

  static List<Resource> convertToResourceResponse(List<ResourceInfo> resourceInfos) {
    return resourceInfos.stream()
            .map(Converters::convertToResponse
            )
            .collect(Collectors.toList());
  }
}
