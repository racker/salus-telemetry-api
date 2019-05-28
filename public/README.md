
This module contains the public/tenant facing APIs.

# Running locally, non-secure

With the default configuration, the Public API application will run non-secure and bind to port 8080.
The GraphQL API can be invoked interactively at http://localhost:8080/graphiql

# Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.

# Example GraphQL operations

## Agent Installation Operations

### Get Agent Releases

```
GET localhost:8080/api/agentReleases
```

### Install Agent Release

```
POST localhost:8080/api/agentInstalls
Content-Type: application/json

{
	"agentReleaseId": "e93149f7-79de-4517-940c-4c956166e5e3",
	"labelSelector": {
		"agent_discovered_os": "darwin"
	}
}
```

## Monitor Operations

To be documented...

### Create monitor

### Get all monitors

### Delete monitor


## Resource Operations

To be documented...

### Create a resource

### Get all resources

### Delete a resource
