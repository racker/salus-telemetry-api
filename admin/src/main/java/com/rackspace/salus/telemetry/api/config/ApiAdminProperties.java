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

import com.rackspace.salus.telemetry.model.LabelNamespaces;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("salus.api.admin")
@Component
@Data
@Validated
public class ApiAdminProperties {

  /**
   * The roles (without "ROLE_" prefix) that are required to allow the user to make use of tenant APIs.
   * Identity roles are translated to this format via {@link com.rackspace.salus.common.web.PreAuthenticatedFilter}.
   */
  @NotEmpty
  List<String> roles = new ArrayList<>();

  /**
   * When registering an agent release, these are the labels that are required to be present
   * in the selector.
   */
  @NotEmpty
  List<String> requiredAgentLabels = Arrays.asList(
      LabelNamespaces.applyNamespace(LabelNamespaces.AGENT, "discovered_os"),
      LabelNamespaces.applyNamespace(LabelNamespaces.AGENT, "discovered_arch")
  );
}
