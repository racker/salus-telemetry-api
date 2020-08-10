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

package com.rackspace.salus.telemetry.api.services;

import com.rackspace.salus.common.web.RemoteServiceCallException;
import com.rackspace.salus.event.manage.model.TestTaskRequest;
import com.rackspace.salus.event.manage.model.TestTaskResult;
import com.rackspace.salus.event.manage.web.client.EventTaskApi;
import com.rackspace.salus.monitor_management.web.client.MonitorApi;
import com.rackspace.salus.monitor_management.web.model.TestMonitorInput;
import com.rackspace.salus.monitor_management.web.model.TestMonitorResult;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskRequest;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask.TestMonitorResultData;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.TestMonitorAndEventTask.TestTaskResultResultData;
import com.rackspace.salus.telemetry.model.SimpleNameTagValueMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestMonitorAndEventTaskService {

  private final MonitorApi monitorApi;
  private final EventTaskApi eventTaskApi;

  @Autowired
  public TestMonitorAndEventTaskService(MonitorApi monitorApi, EventTaskApi eventTaskApi) {
    this.monitorApi = monitorApi;
    this.eventTaskApi = eventTaskApi;
  }

  public TestMonitorAndEventTaskResponse performTestMonitorAndEventTask(String tenantId,
      TestMonitorAndEventTaskRequest testMonitorAndEventTaskRequest) {
    TestMonitorResult testMonitorResult = null;
    TestTaskResult testTaskResult = null;
    try {
      testMonitorResult = monitorApi
          .performTestMonitor(tenantId, new TestMonitorInput()
              .setResourceId(testMonitorAndEventTaskRequest.getResourceId())
              .setDetails(testMonitorAndEventTaskRequest.getDetails()));

      if (testMonitorResult == null || !CollectionUtils.isEmpty(testMonitorResult.getErrors())
          || CollectionUtils
          .isEmpty(testMonitorResult.getData().getMetrics())) {
        return new TestMonitorAndEventTaskResponse().setErrors(
            CollectionUtils.isEmpty(testMonitorResult.getErrors()) ? List
                .of("Unable to get test-Monitor Metrics") : testMonitorResult.getErrors());
      }

      List<SimpleNameTagValueMetric> metrics = testMonitorResult.getData().getMetrics().stream()
          .filter(
              e -> e.getName().equals(testMonitorAndEventTaskRequest.getTask().getMeasurement()))
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(metrics)) {
        return new TestMonitorAndEventTaskResponse()
            .setData(new TestMonitorAndEventTask().setMonitor(
                new TestMonitorResultData().setMetrics(testMonitorResult.getData().getMetrics())))
            .setErrors(List.of("Unable to find matching metric name"));
      }

      TestTaskRequest testTaskRequest = new TestTaskRequest()
          .setTask(testMonitorAndEventTaskRequest.getTask())
          .setMetrics(metrics);

      testTaskResult = eventTaskApi
          .performTestTask(tenantId, testTaskRequest);

      if (testTaskResult == null || !CollectionUtils.isEmpty(testTaskResult.getErrors())
          || testTaskResult.getData() == null) {
        return new TestMonitorAndEventTaskResponse().setData(new TestMonitorAndEventTask()
            .setMonitor(
                new TestMonitorResultData().setMetrics(testMonitorResult.getData().getMetrics())))
            .setErrors(CollectionUtils.isEmpty(testMonitorResult.getErrors()) ? List
                .of("Unable to get test event data") : testMonitorResult.getErrors());
      }

      return new TestMonitorAndEventTaskResponse().setData(new TestMonitorAndEventTask()
          .setMonitor(
              new TestMonitorResultData().setMetrics(testMonitorResult.getData().getMetrics()))
          .setTask(new TestTaskResultResultData().setEvents(testTaskResult.getData().getEvents())
              .setStats(testTaskResult.getData()
                  .getStats())));
    } catch (RemoteServiceCallException e) {
      TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse();
      if (testMonitorResult.getData() != null) {
        testMonitorAndEventTaskResponse.setData(
            new TestMonitorAndEventTask().setMonitor(
                new TestMonitorResultData().setMetrics(testMonitorResult.getData().getMetrics())));
      } else if (testTaskResult.getData() != null) {
        new TestMonitorAndEventTask().setTask(
            new TestTaskResultResultData().setEvents(testTaskResult.getData().getEvents())
                .setStats(testTaskResult.getData()
                    .getStats()));
      } else {
        return new TestMonitorAndEventTaskResponse().setErrors(List.of(String
            .format("An unexpected internal error occurred: %s", e.getMessage())));
      }
    }
    return null;
  }
}