/*
 *    Copyright 2018 Rackspace US, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package com.rackspace.salus.telemetry.api;

import com.rackspace.salus.telemetry.api.tenant.TenantAgentApi;
import com.rackspace.salus.telemetry.model.NotFoundException;
import java.util.Date;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice(basePackageClasses = {
    TenantAgentApi.class
})
public class ApiControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorContent> handleNotFound(WebRequest request, NotFoundException ex) {
        return handleWithStatus(ex, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorContent> handleWithStatus(Throwable ex, HttpStatus status) {
        final ErrorContent errorContent = new ErrorContent();
        errorContent.setTimestamp(new Date());
        errorContent.setStatus(status.value());
        errorContent.setError(status.getReasonPhrase());
        errorContent.setMessage(ex.getMessage());

        return ResponseEntity.status(status).body(errorContent);
    }

    /**
     * Mimics the usual content provided by {@link org.springframework.boot.web.servlet.error.DefaultErrorAttributes}
     * without all the servlet ceremony that it expects.
     */
    @Data
    static class ErrorContent {
        Date timestamp;
        int status;
        String error;
        String message;
    }
}
