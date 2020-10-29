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
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.ResponseData;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.ResponseData.TestMonitorResultData;
import com.rackspace.salus.telemetry.api.model.TestMonitorAndEventTaskResponse.ResponseData.TestTaskResultData;
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
    final TestMonitorAndEventTaskResponse testMonitorAndEventTaskResponse = new TestMonitorAndEventTaskResponse();
    try {
      TestMonitorResult testMonitorResult = monitorApi
          .performTestMonitor(tenantId, new TestMonitorInput()
              .setResourceId(testMonitorAndEventTaskRequest.getResourceId())
              .setDetails(testMonitorAndEventTaskRequest.getDetails()));

      if (testMonitorResult == null) {
        testMonitorAndEventTaskResponse.setErrors(List.of("Unable to get test-Monitor Metrics"));
        return testMonitorAndEventTaskResponse;
      } else if (!CollectionUtils.isEmpty(testMonitorResult.getErrors())) {
        testMonitorAndEventTaskResponse.setErrors(testMonitorResult.getErrors());
        return testMonitorAndEventTaskResponse;
      } else if (CollectionUtils.isEmpty(testMonitorResult.getData().getMetrics())) {
        testMonitorAndEventTaskResponse.setErrors(List.of("Unable to get test-Monitor Metrics"));
        return testMonitorAndEventTaskResponse;
      }
      testMonitorAndEventTaskResponse.setData(new ResponseData().setMonitor(
          new TestMonitorResultData().setMetrics(testMonitorResult.getData().getMetrics())));

      // Validating requested measurement against collected TestMonitorResult metrics measurements
      List<SimpleNameTagValueMetric> metricsForTask = testMonitorResult.getData().getMetrics()
          .stream()
//          TODO this will change once test-task is updated for the esper event engine
//          .filter(
//              e -> e.getName().equals(testMonitorAndEventTaskRequest.getTask().getMeasurement()))
          .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(metricsForTask)) {
        testMonitorAndEventTaskResponse.setErrors(List.of("Unable to find matching metric name"));
        return testMonitorAndEventTaskResponse;
      }

      TestTaskRequest testTaskRequest = new TestTaskRequest()
          .setTask(testMonitorAndEventTaskRequest.getTask())
          .setMetrics(metricsForTask);

      TestTaskResult testTaskResult = eventTaskApi
          .performTestTask(tenantId, testTaskRequest);

      if (testTaskResult == null) {
        testMonitorAndEventTaskResponse
            .setErrors(CollectionUtils.isEmpty(testMonitorResult.getErrors()) ? List
                .of("Unable to get test event data") : testMonitorResult.getErrors());
        return testMonitorAndEventTaskResponse;
      } else if (!CollectionUtils.isEmpty(testTaskResult.getErrors())) {
        testMonitorAndEventTaskResponse.setErrors(testMonitorResult.getErrors());
        return testMonitorAndEventTaskResponse;
      } else if (testTaskResult.getData() == null) {
        testMonitorAndEventTaskResponse
            .setErrors(List.of("Unable to get test event data"));
        return testMonitorAndEventTaskResponse;
      }

      testMonitorAndEventTaskResponse.getData()
          .setTask(new TestTaskResultData().setEvents(testTaskResult.getData().getEvents())
              .setStats(testTaskResult.getData()
                  .getStats()));
      return testMonitorAndEventTaskResponse;
    } catch (RemoteServiceCallException e) {
      testMonitorAndEventTaskResponse.setErrors(List.of(String
          .format("An unexpected internal error occurred: %s", e.getMessage())));
      return testMonitorAndEventTaskResponse;
    }
  }
}