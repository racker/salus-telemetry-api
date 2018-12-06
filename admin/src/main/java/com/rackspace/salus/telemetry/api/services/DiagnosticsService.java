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
import com.rackspace.salus.telemetry.api.model.KVEntry;
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

  public CompletableFuture<KVEntry> getKey(String key) {
    return etcd.getKVClient().get(
        ByteSequence.fromString(key)
    )
        .thenApply(getResponse -> {
          if (getResponse.getCount() == 0) {
            return null;
          } else {
            return buildKVEntry(getResponse.getKvs().get(0));
          }
        });
  }

  public CompletableFuture<Long> deleteKey(String key) {
    return etcd.getKVClient()
        .delete(ByteSequence.fromString(key))
        .thenApply(DeleteResponse::getDeleted);
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

  public CompletableFuture<List<KVEntry>> getKeys(String prefix) {
    if (prefix == null) {
      // we tend to start prefixes with a slash, so its a good fallback
      prefix = "/";
    }
    final ByteSequence prefixBytes = ByteSequence.fromString(prefix);

    return etcd.getKVClient()
        .get(
            prefixBytes,
            GetOption.newBuilder()
                .withPrefix(prefixBytes)
                .build()
        )
        .thenApply(getResponse ->
            getResponse.getKvs().stream()
                .map(this::buildKVEntry)
                .collect(Collectors.toList())
        );

  }

  private KVEntry buildKVEntry(KeyValue kv) {
    return new KVEntry()
        .setName(kv.getKey().toStringUtf8())
        .setValue(kv.getValue().toStringUtf8())
        .setCreateRevision(kv.getCreateRevision())
        .setModRevision(kv.getModRevision())
        .setLease(kv.getLease())
        .setVersion(kv.getVersion());
  }
}
