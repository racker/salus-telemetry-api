
This module contains the APIs for interacting with and managing a Salus Telemetry system.
The API includes both user/tenant facing operations and system admin facing operations.

# Building

This module is a Spring Boot application and can be built with the usual Maven process of

```bash
mvn package
```

# Running locally 

With the default configuration and the Spring "dev" profile activated, 
the Admin API application will run non-secure and bind to port 8888.

If you want to simulate the behavior of running behind a SAML authentication proxy, then
you can can activate the "proxied-auth" Spring profile. The user and groups need to
be passed with each request via the headers configured via binding of 
`com.rackspace.salus.telemetry.api.config.ApiAdminProperties`.

# Example Queries

## Declare agent release

```graphql
mutation DeclareAgentRelease($release:AgentReleaseInput!) {
  declareAgentRelease(input:$release) {
    id
  }
}
```

given
```json
{
	"release": {
		"type": "TELEGRAF",
		"version": "1.9.4",
		"labelSelector": [
			{
				"name": "agent_discovered_os",
				"value": "darwin"
			},
			{
				"name": "agent_discovered_arch",
				"value": "amd64"
			}
		],
		"url": "https://homebrew.bintray.com/bottles/telegraf-1.9.4.high_sierra.bottle.tar.gz",
		"exe": "telegraf/1.9.4/bin/telegraf",
		"checksum": {
			"value": "",
			"type": "SHA512"
		}
	}
}
```

## Change work partition count

```graphql
mutation 
{
  changePresenceMonitorPartitions(count:32) {
    success
  }
}
```

## Query work partitions

```graphql
{
  presenceMonitorPartitions {
    start
    end
  }
}
```
