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

package com.rackspace.salus.telemetry.api.config;

import com.rackspace.salus.event.manage.web.client.EventTaskApi;
import com.rackspace.salus.event.manage.web.client.EventTaskApiClient;
import com.rackspace.salus.monitor_management.web.client.MonitorApi;
import com.rackspace.salus.monitor_management.web.client.MonitorApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientsConfig {

    private final ServicesProperties servicesProperties;

    @Autowired
    public RestClientsConfig(ServicesProperties servicesProperties) {
        this.servicesProperties = servicesProperties;
    }

    @Bean
    public MonitorApi monitorApi(RestTemplateBuilder restTemplateBuilder) {
        return new MonitorApiClient(
                restTemplateBuilder
                        .rootUri(servicesProperties.getMonitorManagementUrl())
                        .build()
        );
    }

    @Bean
    public EventTaskApi eventTaskApi(RestTemplateBuilder restTemplateBuilder) {
        return new EventTaskApiClient(
                restTemplateBuilder
                        .rootUri(servicesProperties.getEventManagementUrl())
                        .build()
        );
    }
}
