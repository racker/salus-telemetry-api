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

package com.rackspace.salus.telemetry.api.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.rackspace.salus.telemetry.api.model.DetailedMonitorInput;
import com.rackspace.salus.telemetry.api.model.DetailedMonitorOutput;
import com.rackspace.salus.telemetry.api.model.LocalMonitorDetails;
import com.rackspace.salus.telemetry.api.model.LocalPlugin;
import com.rackspace.salus.telemetry.api.model.telegraf.Cpu;
import com.rackspace.salus.telemetry.model.AgentType;
import com.rackspace.salus.telemetry.model.ConfigSelectorScope;
import com.rackspace.salus.telemetry.model.Monitor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

@RunWith(SpringRunner.class)
@JsonTest
@Import({MonitorConversionService.class})
public class MonitorConversionServiceTest {

  @Autowired
  MonitorConversionService conversionService;

  @Test
  public void convertToOutput() throws IOException {
    Map<String, String> labels = new HashMap<>();
    labels.put("os", "linux");
    labels.put("test", "convertToOutput");

    final String content = readContent("/MonitorConversionServiceTest_cpu.json");

    final UUID monitorId = UUID.randomUUID();

    Monitor monitor = new Monitor()
        .setId(monitorId)
        .setMonitorName("name-a")
        .setAgentType(AgentType.TELEGRAF)
        .setSelectorScope(ConfigSelectorScope.ALL_OF)
        .setLabels(labels)
        .setContent(content);

    final DetailedMonitorOutput result = conversionService.convertToOutput(monitor);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(monitorId.toString());
    assertThat(result.getName()).isEqualTo("name-a");
    assertThat(result.getLabels()).isEqualTo(labels);
    assertThat(result.getDetails()).isInstanceOf(LocalMonitorDetails.class);

    final LocalPlugin plugin = ((LocalMonitorDetails) result.getDetails()).getPlugin();
    assertThat(plugin).isInstanceOf(Cpu.class);

    final Cpu cpuPlugin = (Cpu) plugin;
    assertThat(cpuPlugin.isCollectCpuTime()).isFalse();
    assertThat(cpuPlugin.isPercpu()).isFalse();
    assertThat(cpuPlugin.isReportActive()).isFalse();
    assertThat(cpuPlugin.isTotalcpu()).isFalse();
  }

  @Test
  public void convertFromInput() throws JSONException, IOException {
    final Map<String, String> labels = new HashMap<>();
    labels.put("os", "linux");
    labels.put("test", "convertFromInput_cpu");

    final LocalMonitorDetails details = new LocalMonitorDetails();
    final Cpu plugin = new Cpu();
    plugin.setPercpu(false);
    details.setPlugin(plugin);

    DetailedMonitorInput input = new DetailedMonitorInput()
        .setName("name-a")
        .setLabels(labels)
        .setDetails(details);
    final Monitor result = conversionService.convertFromInput(input);

    assertThat(result).isNotNull();
    assertThat(result.getLabels()).isEqualTo(labels);
    assertThat(result.getAgentType()).isEqualTo(AgentType.TELEGRAF);
    assertThat(result.getMonitorName()).isEqualTo("name-a");
    assertThat(result.getSelectorScope()).isEqualTo(ConfigSelectorScope.ALL_OF);
    final String content = readContent("/MonitorConversionServiceTest_cpu.json");
    JSONAssert.assertEquals(content, result.getContent(), true);
  }

  private static String readContent(String resource) throws IOException {
    try (InputStream in = new ClassPathResource(resource).getInputStream()) {
      return FileCopyUtils.copyToString(new InputStreamReader(in));
    }
  }
}