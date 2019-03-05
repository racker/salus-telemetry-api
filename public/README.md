
This module contains the public/tenant facing APIs.

# Running locally, non-secure

With the default configuration, the Public API application will run non-secure and bind to port 8080.
The GraphQL API can be invoked interactively at http://localhost:8080/graphiql

# Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.

# Example GraphQL operations

## Agent Installation Operations

### Install Agent Release

```graphql
mutation InstallAgentRelease($releaseId:String!, $labels:[LabelInput!]!)
{
  installAgentRelease(agentReleaseId:$releaseId, labels:$labels) {
    id
  }
}
```

given

```json
{
  "releaseId": "ea99d592-fbc3-452a-9b17-5b0e0b1325bd",
  "labels": [
    {
      "name": "os",
      "value": "DARWIN"
    }
  ]
}
```

## Monitor Operations

### Create monitor

```graphql
mutation {
  createLocalMonitor(
    matchingLabels: [{ name: "os", value: "DARWIN" }]
    configs: {
      usingTelegraf: { 
        mem: { enabled: true }, 
        cpu: { enabled: true },
      	disk: { enabled:true, mountPoints:"/var/lib" }
      }
    }
  ) {
    id
  }
}
```

### Get all monitors

```graphql
{
  monitors {
    totalElements
    first
    last
    content {
      id
      matchingLabels {
        name
        value
      }
      configs {
        local {
          usingTelegraf {
            enabled
            configJson
          }
        }
      }
    }
  }
}
```

### Delete monitor

```graphql
mutation {
  deleteMonitor(id:"e20d16c0-c1dd-46f6-8cac-577100c0f341") {
    success
  }
}
```

## Resource Operations

### Create a resource

```graphql
mutation CreateResource($r:ResourceInput!) {
  createResource(resource:$r) {
    identifierName
    identifierValue
    envoyId
    address
  }
}
```

given

```json
{
  "r": {
    "identifierName": "myid",
    "labels": {
      "name": "myid",
      "value": "thingsandstuff"
    }
  }
}
```

### Get all resources
```graphql
query GetAllResources {
  resources {
    tenantId
    identifierName
    identifierValue
    address
    envoyId
    labels {
      name
      value
    }
  }
}
```

### Delete a resource
Must specify the identifierName and value.

```graphql
mutation DeleteResource ($i:String!, $iv:String!) {
  deleteResource(identifierName:$i, identifierValue:$iv) {
    success
  }
}
```

given

```json
{
  "i": "arch",
  "iv": "X86_64"
}
```
