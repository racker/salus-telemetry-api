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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rackspace.salus.telemetry.api.model.Using;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonInclude(Include.NON_NULL)
@Data @EqualsAndHashCode(callSuper = true)
public class UsingLocalTelegraf extends Using {
  Cpu cpu;
  Disk disk;
  DiskIo diskio;
  Mem mem;

  public List<String> buildEnabledList() {
    final List<String> plugins = new ArrayList<>();

    markEnabled(plugins, "cpu", cpu);
    markEnabled(plugins, "disk", disk);
    markEnabled(plugins, "diskio", diskio);
    markEnabled(plugins, "mem", mem);

    return plugins;
  }

  private static void markEnabled(List<String> plugins, String name, TelegrafPlugin plugin) {
    if (plugin != null && plugin.isEnabled()) {
      plugins.add(name);
    }
  }

}
