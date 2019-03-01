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

package com.rackspace.salus.telemetry.api.model.input;

import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.Architecture;
import com.rackspace.salus.telemetry.model.OperatingSystem;
import io.leangen.graphql.annotations.GraphQLNonNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Setter(onParam = @__({@GraphQLNonNull})) // GraphQL SPQR only looks at the setter property for required-field calculation
public class AgentRelease {
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
  ExpectedChecksum checksum;

  /**
   * Path to the agent's executable within the package
   */
  @NotBlank
  String exe;
}
