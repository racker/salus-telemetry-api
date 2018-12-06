package com.rackspace.salus.telemetry.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("dev")
public class DevProperties {

  /**
   * The username/principal to be used for unauthenticated users. It should probably match the
   * tenant ID you are using for Envoy testing.
   */
  String anonymousUsername = "aaaaaa";
}
