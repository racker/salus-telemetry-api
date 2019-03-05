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

package com.rackspace.salus.telemetry.api.model.telegraf;

import io.leangen.graphql.annotations.GraphQLNonNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data @EqualsAndHashCode(callSuper = true)
public class Ping extends RemoteTelegrafPlugin {
  @Setter(onParam = @__({@GraphQLNonNull}))
  List<@GraphQLNonNull String> urls;
  Integer count;
  Integer pingInterval;
  Integer timeout;
  Integer deadline;
  String interfaceOrAddress;

  // DO NOT include 'binary' or 'arguments' from telegraf raw config since those would expose an
  // exploitable attack vector on the customer servers
}
