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

package com.rackspace.salus.telemetry.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rackspace.salus.telemetry.api.config.ServicesProperties;
import com.rackspace.salus.telemetry.api.web.ZonesController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;


/**
 * These tests verify that we are proxying requests to the internal services
 * via the correct url.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes={ZonesController.class})
public class ZonesControllerTest {

  @MockBean
  ServicesProperties servicesProperties;

  @Autowired
  ZonesController zonesController;

  @Mock
  ProxyExchange proxyExchange;

  @Before
  public void setup() {
    when(servicesProperties.getMonitorManagementUrl())
        .thenReturn("http://test/");
    when(proxyExchange.path(anyString()))
        .thenReturn("public/dev");
    when(proxyExchange.uri(anyString()))
        .thenReturn(proxyExchange);
    when(proxyExchange.body(anyString()))
        .thenReturn(proxyExchange);
    when(proxyExchange.get())
        .thenReturn(null);
    when(proxyExchange.put())
        .thenReturn(null);
    when(proxyExchange.post())
        .thenReturn(null);
  }

  @Test
  public void testAllGetPublicZones() {
    zonesController.getAll(proxyExchange, new HttpHeaders(), new LinkedMultiValueMap<>());
    verify(proxyExchange).uri("http://test/api/admin/zones");
  }

  @Test
  public void testGetPublicZone() {
    zonesController.get(proxyExchange, new HttpHeaders(), new LinkedMultiValueMap<>());
    verify(proxyExchange).uri("http://test/api/admin/zones/public/dev");
  }

  @Test
  public void testCreatePublicZone() {
    zonesController.create(proxyExchange, new HttpHeaders());
    verify(proxyExchange).uri("http://test/api/admin/zones");
  }

  @Test
  public void testUpdatePublicZone() {
    zonesController.update(proxyExchange, new HttpHeaders());
    verify(proxyExchange).uri("http://test/api/admin/zones/public/dev");
  }

  @Test
  public void testDeletePublicZone() {
    zonesController.delete(proxyExchange, new HttpHeaders());
    verify(proxyExchange).uri("http://test/api/admin/zones/public/dev");
  }

  @Test
  public void testGetPrivateZoneAssignmentCounts() {
    zonesController.getPrivateZoneAssignmentCounts(proxyExchange, new HttpHeaders());
    verify(proxyExchange).uri("http://test/api/admin/zone-assignment-counts/public/dev");
  }

  @Test
  public void testRebalancePublicZone() {
    zonesController.rebalancePublicZone(proxyExchange, new HttpHeaders());
    verify(proxyExchange).uri("http://test/api/admin/rebalance-zone/public/dev");
  }
}
