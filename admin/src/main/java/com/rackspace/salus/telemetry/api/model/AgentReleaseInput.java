package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.Architecture;
import com.rackspace.salus.telemetry.model.OperatingSystem;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgentReleaseInput {
  @NotBlank
  String version;

  @NotNull
  AgentType type;

  @NotNull
  OperatingSystem os;

  @NotNull
  Architecture arch;

  @NotBlank
  String url;

  @NotNull
  ChecksumInput checksum;

  /**
   * Path to the agent's executable within the package
   */
  @NotBlank
  String exe;
}
