/*
 * Copyright 2019 Rackspace US, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rackspace.salus.telemetry.api.config;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("salus.services")
@Component
@Data
@Validated
public class ServicesProperties {
  @NotEmpty
  String monitorManagementUrl = "http://monitor-management:8080";
  @NotEmpty
  String resourceManagementUrl = "http://resource-management:8080";
  @NotEmpty
  String eventManagementUrl = "http://event-management:8080";
  @NotEmpty
  String presenceMonitorUrl = "http://presence-monitor:8080";
  @NotEmpty
  String agentCatalogManagementUrl = "http://agent-catalog-management:8080";
  @NotEmpty
  String policyManagementUrl = "http://policy-management:8080";
}
