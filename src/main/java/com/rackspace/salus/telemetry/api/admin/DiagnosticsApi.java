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

package com.rackspace.salus.telemetry.api.admin;

import com.rackspace.salus.telemetry.api.services.DiagnosticsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/diagnostics")
public class DiagnosticsApi {

    private final DiagnosticsService diagnosticsService;

    @Autowired
    public DiagnosticsApi(DiagnosticsService diagnosticsService) {
        this.diagnosticsService = diagnosticsService;
    }

    @GetMapping("/getAllKeys")
    public CompletableFuture<List<String>> getAllKeys() {
        return diagnosticsService.getAllKeys();
    }

    @GetMapping("/getKey")
    public CompletableFuture<KeyResponse> getKey(@RequestParam String key) {
        return diagnosticsService.getKey(key);
    }

    @DeleteMapping("/deleteKey")
    public CompletableFuture<DeleteResponse> deleteKey(@RequestParam String key) {
        return diagnosticsService.deleteKey(key)
                .thenApply(deleted -> new DeleteResponse(deleted ? 1 : 0));
    }

    @DeleteMapping("/deleteKeys")
    public CompletableFuture<DeleteResponse> deleteKeys(@RequestParam(required = false) String prefix) {
        if (!StringUtils.isEmpty(prefix)) {
            return diagnosticsService.deleteKeysByPrefix(prefix)
                    .thenApply(DeleteResponse::new);
        }
        else {
            throw new IllegalArgumentException("Missing specification of keys to delete. Try 'prefix'");
        }
    }
}
