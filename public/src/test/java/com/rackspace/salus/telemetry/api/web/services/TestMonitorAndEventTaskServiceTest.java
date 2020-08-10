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

package com.rackspace.salus.telemetry.api.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rackspace.salus.event.manage.model.TestTaskRequest;
import com.rackspace.salus.event.manage.model.TestTaskResult;
import com.rackspace.salus.event.manage.model.TestTaskResult.TestTaskResultData;
import com.rackspace.salus.event.manage.model.TestTaskResult.TestTaskResultData.EventResult;
import com.rackspace.salus.event.manage.web.client.EventTaskApi;
import com.rackspace.salus.event.model.kapacitor.KapacitorEvent.EventData;
import com.rackspace.salus.event.model.kapacitor.KapacitorEvent.SeriesItem;
import com.rackspace.salus.event.model.kapacitor.Task.Stats;
import com.rackspace.salus.monitor_management.web.client.MonitorApi;
import com.rackspace.salus.monitor_management.web.model.TestMonitorInput;
import com.rackspace.salus.monitor_management.web.model.TestMonitorResult;
import com.rackspace.salus.monitor_management.web.model.TestMonitorResult.TestMonitorResultData;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskRequest;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask.TestTaskResultResultData;
import com.rackspace.salus.telemetry.api.services.TestMonitorAndEventTaskService;
import com.rackspace.salus.telemetry.model.SimpleNameTagValueMetric;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Map;

import static com.rackspace.salus.common.util.SpringResourceUtils.readContent;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "salus.api.public.roles: IDENTITY_DEFAULT"
})
public class TestMonitorAndEventTaskServiceTest {

  private PodamFactory podamFactory = new PodamFactoryImpl();

  @Autowired
  private TestMonitorAndEventTaskService testMonitorAndEventTaskService;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  MonitorApi monitorApi;

  @MockBean
  EventTaskApi eventTaskApi;

  @Test
  public void testPerformTestMonitorAndEventTask() throws Exception {
    TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest = objectMapper
        .readValue(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_req.json"),
            TestMonitorAndEventTaskRequest.class);
    String tenantId = RandomStringUtils.randomAlphabetic(8);

    TestMonitorResult testMonitorResult = new TestMonitorResult()
        .setData(new TestMonitorResultData().setMetrics(List.of(
            new SimpleNameTagValueMetric()
                .setName(testMonitorAndEventTaskRequest.getTask().getMeasurement())
                .setFvalues(Map.of("available_percent", 30.973100662231445))
        )));

    TestMonitorInput testMonitorInput = new TestMonitorInput()
        .setResourceId(testMonitorAndEventTaskRequest.getResourceId())
        .setDetails(testMonitorAndEventTaskRequest.getDetails());

    when(monitorApi.performTestMonitor(tenantId, testMonitorInput)).thenReturn(testMonitorResult);

    TestTaskResult testTaskResult = new TestTaskResult()
        .setData(new TestTaskResultData().setEvents(List.of(
            new EventResult()
                .setData(
                    new EventData()
                        .setSeries(List.of(
                            new SeriesItem()
                                .setName(testMonitorAndEventTaskRequest.getTask().getMeasurement())
                        ))
                )
                .setLevel("CRITICAL")
        )).setStats(
            new Stats()
                .setNodeStats(Map.of("alert2", Map.of("crits_triggered", 1)))
        ));

    TestTaskRequest testTaskRequest = new TestTaskRequest()
        .setTask(testMonitorAndEventTaskRequest.getTask())
        .setMetrics(List.of(
            new SimpleNameTagValueMetric().setName("mem")
                .setFvalues(Map.of("available_percent", 30.973100662231445))));

    when(eventTaskApi.performTestTask(tenantId, testTaskRequest)).thenReturn(testTaskResult);

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse()
        .setData(
            new TestMonitorAndEventTask()
                .setMonitor(new TestMonitorAndEventTask.TestMonitorResultData()
                    .setMetrics(testMonitorResult.getData().getMetrics()))
                .setTask(
                    new TestTaskResultResultData().setStats(testTaskResult.getData().getStats())
                        .setEvents(testTaskResult.getData().getEvents())));

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponseActual = testMonitorAndEventTaskService
        .performTestMonitorAndEventTask(tenantId, testMonitorAndEventTaskRequest);

    assertThat(testMonitorAndEventTaskResponseActual.getData().getMonitor(), notNullValue());

    assertThat(testMonitorAndEventTaskResponseActual.getData().getTask(), notNullValue());
    assertEquals(testMonitorAndEventTaskResponse, testMonitorAndEventTaskResponseActual);

    verify(monitorApi).performTestMonitor(tenantId, testMonitorInput);
    verify(eventTaskApi).performTestTask(tenantId, testTaskRequest);
  }

  @Test
  public void testPerformTestMonitorAndEventTask_Fail_Test_Monitor() throws Exception {
    TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest = objectMapper
        .readValue(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_req.json"),
            TestMonitorAndEventTaskRequest.class);
    testMonitorAndEventTaskRequest.setResourceId("development");
    String tenantId = RandomStringUtils.randomAlphabetic(8);

    TestMonitorResult testMonitorResult = new TestMonitorResult()
        .setErrors(List.of("Unable to locate the resource for the test-monitor"));

    TestMonitorInput testMonitorInput = new TestMonitorInput()
        .setResourceId(testMonitorAndEventTaskRequest.getResourceId())
        .setDetails(testMonitorAndEventTaskRequest.getDetails());

    when(monitorApi.performTestMonitor(tenantId, testMonitorInput)).thenReturn(testMonitorResult);

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse()
        .setErrors(List.of("Unable to locate the resource for the test-monitor"));

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponseActual = testMonitorAndEventTaskService
        .performTestMonitorAndEventTask(tenantId, testMonitorAndEventTaskRequest);

    assertThat(testMonitorAndEventTaskResponseActual.getData(), nullValue());
    assertEquals(testMonitorAndEventTaskResponse.getErrors(),
        testMonitorAndEventTaskResponseActual.getErrors());
  }

  @Test
  public void testPerformTestMonitorAndEventTask_Fail_EventTask() throws Exception {
    TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest = objectMapper
        .readValue(
            readContent("PerformTestMonitorTaskEvent/testPerformTestMonitorAndEventTask_req.json"),
            TestMonitorAndEventTaskRequest.class);
    String tenantId = RandomStringUtils.randomAlphabetic(8);

    TestMonitorResult testMonitorResult = new TestMonitorResult()
        .setData(new TestMonitorResultData().setMetrics(List.of(
            new SimpleNameTagValueMetric()
                .setName(testMonitorAndEventTaskRequest.getTask().getMeasurement())
                .setFvalues(Map.of("available_percent", 30.973100662231445))
        )));

    TestMonitorInput testMonitorInput = new TestMonitorInput()
        .setResourceId(testMonitorAndEventTaskRequest.getResourceId())
        .setDetails(testMonitorAndEventTaskRequest.getDetails());

    when(monitorApi.performTestMonitor(tenantId, testMonitorInput)).thenReturn(testMonitorResult);

    TestTaskResult testTaskResult = new TestTaskResult()
        .setErrors(List.of("Timed out waiting for test-event-task result"));

    TestTaskRequest testTaskRequest = new TestTaskRequest()
        .setTask(testMonitorAndEventTaskRequest.getTask())
        .setMetrics(List.of(
            new SimpleNameTagValueMetric().setName("mem")
                .setFvalues(Map.of("available_percent1", 30.973100662231445))));

    when(eventTaskApi.performTestTask(tenantId, testTaskRequest)).thenReturn(testTaskResult);

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse()
        .setData(
            new TestMonitorAndEventTask().setMonitor(
                new TestMonitorAndEventTask.TestMonitorResultData()
                    .setMetrics(testMonitorResult.getData().getMetrics())))
        .setErrors(List.of("Unable to get test event data"));

    TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponseActual = testMonitorAndEventTaskService
        .performTestMonitorAndEventTask(tenantId, testMonitorAndEventTaskRequest);

    assertThat(testMonitorAndEventTaskResponseActual.getData().getMonitor(), notNullValue());

    assertThat(testMonitorAndEventTaskResponseActual.getData().getTask(), nullValue());
    assertEquals(testMonitorAndEventTaskResponse, testMonitorAndEventTaskResponseActual);

    verify(monitorApi).performTestMonitor(tenantId, testMonitorInput);
  }
}
