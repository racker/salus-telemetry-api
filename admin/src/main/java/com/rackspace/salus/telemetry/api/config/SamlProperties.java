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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("saml")
@Data
public class SamlProperties {
  String metadataLocation = "file:FederationMetadata.xml";

  String keystoreLocation = "file:samlKeystore.jks";
  String idpTokenSigningAlias = "idpTokenSigning";
  String keystorePassword = "devonly";
  String keyAlias = "boot";
  String keyPassword = "devonly";
  String entityBaseUrl = "https://salus-telemetry-admin-local.area51.rax.io:8443";
  /**
   * The allowed clock skew in SAML reesponse provided by IdP
   */
  int responseSkew = 60;

  String entityId = "salus-telemetry-admin-local.area51.rax.io";

  /**
   * Provides the option to solve SAML issues behind a proxy.
   * See https://stackoverflow.com/a/24809400/121324
   */
  boolean usingLbContextProvider;
}
