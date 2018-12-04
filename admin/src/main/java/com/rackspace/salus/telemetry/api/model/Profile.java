package com.rackspace.salus.telemetry.api.model;

import java.util.List;
import lombok.Data;

@Data
public class Profile {

  String username;
  List<String> authorities;
}
