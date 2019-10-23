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

package com.rackspace.salus.telemetry.api.web;

import brave.Span;
import brave.Tracer;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/**
 * This filter populates all non-error responses with the header {@value #HEADER_TRACE_ID} set to
 * the Spring Cloud Sleuth Trace ID of the overall request.
 * <p>
 *   The majority of this code was derived from the
 *   <a href="https://cloud.spring.io/spring-cloud-sleuth/reference/html/index.html#tracingfilter">Spring Cloud Sleuth documentation.</a>
 * </p>
 */
@Component
@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER+1)
public class TraceResponseFilter extends GenericFilterBean {

  private static final String HEADER_TRACE_ID = "X-Trace-Id";

  private final Tracer tracer;

  @Autowired
  public TraceResponseFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp,
                       FilterChain chain) throws IOException, ServletException {
    final Span currentSpan = tracer.currentSpan();
    if (currentSpan == null) {
      chain.doFilter(req, resp);
      return;
    }

    ((HttpServletResponse) resp).addHeader(HEADER_TRACE_ID, currentSpan.context().traceIdString());

    chain.doFilter(req, resp);
  }
}
