/*
 * Copyright 2020 Rackspace US, Inc.
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

package com.rackspace.salus.telemetry.api.web.client;

import static com.rackspace.salus.common.util.SpringResourceUtils.readContent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rackspace.salus.event.manage.model.TestTaskResult;
import com.rackspace.salus.event.manage.model.TestTaskResult.EventResult;
import com.rackspace.salus.event.manage.model.kapacitor.KapacitorEvent.EventData;
import com.rackspace.salus.event.manage.model.kapacitor.KapacitorEvent.SeriesItem;
import com.rackspace.salus.event.manage.model.kapacitor.Task.Stats;
import com.rackspace.salus.monitor_management.web.model.TestMonitorOutput;
import com.rackspace.salus.telemetry.api.config.ApiPublicProperties;
import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskRequest;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask;
import com.rackspace.salus.telemetry.api.services.TestMonitorAndEventTaskService;
import com.rackspace.salus.telemetry.api.web.TestMonitorController;
import com.rackspace.salus.telemetry.model.SimpleNameTagValueMetric;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ActiveProfiles({"unsecured"})
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TestMonitorController.class)
public class TestMonitorControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  private PodamFactory podamFactory = new PodamFactoryImpl();

  @Autowired
  TestMonitorController testMonitorController;

  @MockBean
  brave.Tracer tracer;

  @MockBean
  ApiPublicProperties apiPublicProperties;

  @MockBean
  TestMonitorAndEventTaskService testMonitorAndEventTaskService;

  @MockBean
  ServicesProperties servicesProperties;

  @Test
  public void testPerformTestMonitorAndEventTask_Success() throws Exception {
    String tenantId = RandomStringUtils.randomAlphabetic(8);
    TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest = objectMapper
        .readValue(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_req.json"),
            TestMonitorAndEventTaskRequest.class);

    final TestTaskResult testTaskResultExpected = new TestTaskResult()
        .setEvents(List.of(
            new EventResult()
                .setData(
                    new EventData()
                        .setSeries(List.of(
                            new SeriesItem()
                                .setName(testMonitorAndEventTaskRequest.getTask().getMeasurement())
                                .setColumns(
                                    List.of("time", "active", "available", "available_percent"))
                                .setValues(List.of(List.of("2020-08-04T18:47:04.9975142Z",
                                    Double.valueOf("5743112192"), Double.valueOf("5755633664"),
                                    Double.valueOf("33.502197265625")
                                    ))
                                )
                        ))
                )
                .setLevel("INFO")
        ))
        .setStats(
            new Stats()
                .setNodeStats(Map.of("alert2", Map.of("crits_triggered", 1)))
                .setTaskStats(Map.of("throughput", 0))
        );

    TestMonitorOutput testMonitorOutputExpected = new TestMonitorOutput()
        .setErrors(List.of())
        .setMetrics(List.of(
            new SimpleNameTagValueMetric()
                .setTags(Map.of())
                .setName(testMonitorAndEventTaskRequest.getTask().getMeasurement())
                .setFvalues(Map.of("available_percent", 33.502197265625))
                .setIvalues(Map.of())
                .setSvalues(Map.of())
        ));

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse(
        new TestMonitorAndEventTask(testMonitorOutputExpected, testTaskResultExpected), List.of());

    when(testMonitorAndEventTaskService.performTestMonitorAndEventTask(anyString(), any()))
        .thenReturn(testMonitorAndEventTaskResponse);

    mockMvc.perform(
        MockMvcRequestBuilders.post("/tenant/{tenantId}/test-monitor-event-task", tenantId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testMonitorAndEventTaskRequest))
            .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(content().json(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_res.json"),
            true));
  }


  @Test
  public void testPerformTestMonitorAndEventTask_Failure() throws Exception {
    String tenantId = RandomStringUtils.randomAlphabetic(8);
    TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest = objectMapper
        .readValue(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_req.json"),
            TestMonitorAndEventTaskRequest.class);

    when(testMonitorAndEventTaskService.performTestMonitorAndEventTask(anyString(), any()))
        .thenThrow(new IllegalArgumentException("Unable to find matching metric name"));

    mockMvc.perform(
        MockMvcRequestBuilders.post("/tenant/{tenantId}/test-monitor-event-task", tenantId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testMonitorAndEventTaskRequest))
            .characterEncoding("utf-8"))
        .andExpect(status().isBadRequest());
  }
}
