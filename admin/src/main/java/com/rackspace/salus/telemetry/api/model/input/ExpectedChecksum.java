package com.rackspace.salus.telemetry.api.model.input;

import com.rackspace.salus.telemetry.model.Checksum;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpectedChecksum {
  @NotNull
  Checksum.Type type;

  @NotEmpty
  String value;
}
