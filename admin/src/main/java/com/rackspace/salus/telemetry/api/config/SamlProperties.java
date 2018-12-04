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
}
