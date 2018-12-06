package com.rackspace.salus.telemetry.api.model;

import com.rackspace.salus.telemetry.model.Checksum;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChecksumInput {
  @NotNull
  Checksum.Type type;

  @NotEmpty
  String value;
}
