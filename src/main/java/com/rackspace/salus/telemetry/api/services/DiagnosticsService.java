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

package com.rackspace.salus.telemetry.api.services;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.DeleteResponse;
import com.coreos.jetcd.options.DeleteOption;
import com.coreos.jetcd.options.GetOption;
import com.rackspace.salus.telemetry.api.admin.KeyResponse;
import com.rackspace.salus.telemetry.model.NotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagnosticsService {

    private final Client etcd;

    @Autowired
    public DiagnosticsService(Client etcd) {
        this.etcd = etcd;
    }

    public CompletableFuture<List<String>> getAllKeys() {
        final CompletableFuture<List<String>> keysFuture = etcd.getKVClient().get(
                ByteSequence.fromString("/"),
                GetOption.newBuilder()
                        .withPrefix(ByteSequence.fromString("/"))
                        .withKeysOnly(true)
                        .build())
                .thenApply(getResponse -> {
                    final List<String> keys = getResponse.getKvs().stream()
                            .map(keyValue -> {
                                return keyValue.getKey().toStringUtf8();
                            })
                            .collect(Collectors.toList());

                    return keys;
                });

        return keysFuture;
    }

    public CompletableFuture<KeyResponse> getKey(String key) {
        return etcd.getKVClient().get(
                ByteSequence.fromString(key)
        )
                .thenApply(getResponse -> {
                    if (getResponse.getCount() == 0) {
                        return null;
                    } else {
                        final KeyResponse keyResponse = new KeyResponse();
                        final KeyValue keyValue = getResponse.getKvs().get(0);

                        keyResponse.setValue(keyValue.getValue().toStringUtf8());
                        keyResponse.setVersion(keyValue.getVersion());
                        keyResponse.setCreateRevision(keyValue.getCreateRevision());
                        keyResponse.setModRevision(keyValue.getModRevision());
                        keyResponse.setLease(keyValue.getLease());

                        return keyResponse;
                    }
                });
    }

    public CompletableFuture<Boolean> deleteKey(String key) {
        return etcd.getKVClient()
                .delete(ByteSequence.fromString(key))
        .thenApply(deleteResponse -> {
            if (deleteResponse.getDeleted() == 0) {
                throw new NotFoundException(String.format("The requested key was not present: %s", key));
            }
            return true;
        });
    }

    public CompletableFuture<Long> deleteKeysByPrefix(String prefix) {
        final ByteSequence prefixBytes = ByteSequence.fromString(prefix);

        return etcd.getKVClient().delete(
                prefixBytes,
                DeleteOption.newBuilder()
                        .withPrefix(prefixBytes)
                        .build()
        )
                .thenApply(DeleteResponse::getDeleted);
    }
}
