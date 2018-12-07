
This module contains the APIs for interacting with and managing a Salus Telemetry system.
The API includes both user/tenant facing operations and system admin facing operations.

## Building

This module is a Spring Boot application and can be built with the usual Maven process of

```bash
mvn package
```

## Working with the SAML based authentication for local testing

### Creating the metadata XML and SAML keystore for development

Using [this wiki page](https://one.rackspace.com/display/GET/Integrating+Your+App+with+AD+FS)
download the IdP metadata XML and save it as `FederationMetadata.xml` in the working directory.

Using [this tool](https://www.rcfed.com/SAMLWSFed/MetadataCertificateExtract) paste the
content of that metadata XML into the field "Extract certificates from Metadata".

Towards the bottom of the output, locate the section "Usage: SAML IDP signing", expand it,
and click download to download the "cer" file.

In this module's working directory copy/convert the baseline keystore:

```bash
keytool -importkeystore \
  -srckeystore src/main/resources/keystore.p12 -srcstorepass devonly \
  -destkeystore samlKeystore.jks -deststoretype jks -deststorepass devonly \
  -keypass devonly
```

Now import the IdP's (identity provider) certificate that was extracted from the IdP metadata:
```bash
keytool -importcert \
  -alias idpTokenSigning \
  -trustcacerts -noprompt \
  -keystore samlKeystore.jks \
  -storepass devonly \
  -file pathToExtractedAndDownloaded.cer
```

> **NOTE** If you use different keystore and key passwords, which you **MUST** in production, you will 
> need to set the application properties `saml.keystore-password` and `saml.key-password` accordingly.

### Running the application

After establishing the files in the previous section, you can run the `TelemetryAdminApiApplication`
entry point with a working directory set to the directory where those files were established.
You will also need to enable the Spring profiles:

* secured
* ssl

With that running you can access the endpoints **in a browser** via HTTPS on port 8443, such as:

    https://salus-telemetry-admin-local.area51.rax.io:8443/admin/profile


## SAML setup for registering with Identity Provider (IdP), such as AD FS

> **NOTE** The process in this section has already been conducted, but is noted here when the 
> need arises to re-configure this in the IdP.

The following is the SP metadata that was provided to the ADFS team:

```xml
<md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" entityID="salus-telemetry-admin-local.area51.rax.io">
   <md:SPSSODescriptor protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
      <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" 
       Location="https://salus-telemetry-admin-local.area51.rax.io:8443/saml/SSO" index="1" />
   </md:SPSSODescriptor>
</md:EntityDescriptor>
```

The public certificate, which was also provided to the ADFS team, was exported using

```bash
keytool -exportcert -keystore keystore.p12 -alias boot -rfc -storepass devonly
```

### Generating the server certificate for local development

If the development-time, self-signed certificate expires or needs to be recreated, then the 
following was used to generate the server's HTTPS keystore `src/main/resources/keystore.p12`

```
keytool -genkey -alias boot \
  -storetype PKCS12 -keyalg RSA -keysize 2048 \
  -keystore keystore.p12 -validity 3650 \
  -storepass devonly
```

When prompted for "your first and last name" enter "salus-telemetry-admin-local.area51.rax.io"
to correspond with the SAML callback location.

## Example Queries

### Declare agent release

```graphql
mutation DeclareAgentRelease($release:AgentReleaseInput!) {
  declareAgentRelease(agentRelease:$release) {
    id
  }
}
```

given
```json
{
  "release": {
    "type": "TELEGRAF",
    "os": "DARWIN",
    "arch": "X86_64",
    "version": "1.8.0",
    "url": "https://homebrew.bintray.com/bottles/telegraf-1.8.0.high_sierra.bottle.tar.gz",
    "exe": "telegraf/1.8.0/bin/telegraf",
    "checksum": {
      "value": "655234590790c420dc8a442c78d71e247d39698525d8fad05a05b67006eb07c7875e6b6e7f184203b59a9a82ee3b91af6b24396a4760b3eb9d236470591e6f89",
      "type": "SHA512"
    }
  }
}
```

### Change work partition count

```graphql
mutation 
{
  changePresenceMonitorPartitions(count:32) {
    success
  }
}
```

### Query work partitions

```graphql
{
  presenceMonitorPartitions {
    start
    end
  }
}
```
