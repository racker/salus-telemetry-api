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

package com.rackspace.salus.telemetry.api.config;

import brave.Span;
import brave.Tracer;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

@Configuration
@EnableConfigurationProperties({ServerProperties.class})
public class TracingErrorAttributesConfig {

  private static final String ATTRIBUTE_TRACE_ID = "traceId";
  private static final String ATTRIBUTE_APP = "app";
  private static final String ATTRIBUTE_HOST = "host";

  /**
   * Registers an extension of the standard Spring Boot {@link ErrorAttributes}
   * that complements the {@link com.rackspace.salus.telemetry.api.web.TraceResponseFilter} by
   * providing extended information for errors that originate at this proxy layer, such as failure
   * to contact a backend service.
   * <p>
   * The Spring Cloud Sleuth Trace ID is populated into the attribute {@value #ATTRIBUTE_TRACE_ID}
   * along with the attributes {@value #ATTRIBUTE_APP} and {@value #ATTRIBUTE_HOST}, similar to
   * {@link com.rackspace.salus.common.web.ExtendedErrorAttributesConfig#errorAttributes(ServerProperties, String, String)}
   * </p>
   */
  @Bean
  public ErrorAttributes errorAttributes(ServerProperties serverProperties,
                                         @Value("${spring.application.name}") String appName,
                                         @Value("${localhost.name}") String ourHost,
                                         Tracer tracer) {
    return new DefaultErrorAttributes(serverProperties.getError().isIncludeException()) {
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                    boolean includeStackTrace) {
        final Map<String, Object> errorAttributes = super
            .getErrorAttributes(webRequest, includeStackTrace);

        errorAttributes.put(ATTRIBUTE_APP, appName);
        errorAttributes.put(ATTRIBUTE_HOST, ourHost);

        final Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
          errorAttributes.put(ATTRIBUTE_TRACE_ID, currentSpan.context().traceIdString());
        }

        return errorAttributes;
      }
    };
  }

}
