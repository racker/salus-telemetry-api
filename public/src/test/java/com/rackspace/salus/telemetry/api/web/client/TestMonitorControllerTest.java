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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rackspace.salus.telemetry.web.TenantVerification;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@RestClientTest
public class TestMonitorControllerTest {
  @Autowired
  MockMvc mockMvc;

  @Test
  public void testCreateTestMonitorAndEventTask_Success() throws Exception {
    String tenantId = RandomStringUtils.randomAlphabetic(8);
    UUID id = UUID.randomUUID();
    String url = String.format("/api/tenant/%s/monitors/%s", tenantId, id);
    String errorMsg = String.format("No monitor found for %s on tenant %s", id, tenantId);

    when(monitorManagement.getMonitor(anyString(), any()))
        .thenReturn(Optional.empty());
    when(tenantMetadataRepository.existsByTenantId(tenantId))
        .thenReturn(true);

    mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)
        // header must be set to trigger tenant verification
        .header(TenantVerification.HEADER_TENANT, tenantId))
        .andExpect(status().isNotFound())
        .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(errorMsg)));

    verify(tenantMetadataRepository).existsByTenantId(tenantId);
  }

  @Test
  public void testCreateTestMonitorAndEventTask_Failure() throws Exception {
    String tenantId = RandomStringUtils.randomAlphabetic(8);
    UUID id = UUID.randomUUID();
    String url = String.format("/api/tenant/%s/monitors/%s", tenantId, id);
    String errorMsg = String.format("No monitor found for %s on tenant %s", id, tenantId);

    when(monitorManagement.getMonitor(anyString(), any()))
        .thenReturn(Optional.empty());
    when(tenantMetadataRepository.existsByTenantId(tenantId))
        .thenReturn(true);

    mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)
        // header must be set to trigger tenant verification
        .header(TenantVerification.HEADER_TENANT, tenantId))
        .andExpect(status().isNotFound())
        .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(errorMsg)));

    verify(tenantMetadataRepository).existsByTenantId(tenantId);
  }
}
