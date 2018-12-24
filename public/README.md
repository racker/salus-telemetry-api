
This module contains the public/tenant facing APIs.

# Running locally, non-secure

With the default configuration, the Public API application will run non-secure and bind to port 8080.
The GraphQL API can be invoked interactively at http://localhost:8080/graphiql

# Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.

# Example GraphQL operations

## Agent Releases Operations

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

## AgentConfig Operations

### Create AgentConfig

```graphql
mutation CreateAgentConfig($config:AgentConfigInput!)
{
  createAgentConfig(config:$config) {
    id
  }
}
```

given

```json
{
  "config": {
    "agentType": "TELEGRAF",
    "selectorScope": "ALL_OF",
    "content": "[[inputs.cpu]]\n[[inputs.disk]]\n  mount_points=[\"/\"]\n[[inputs.mem]]\n",
    "labels": [
      {
        "name": "os",
        "value": "DARWIN"
      }
    ]
  }
}
```

### Get all AgentConfigs

```graphql
query GetAllAgentConfigs
{
  agentConfigs {
    id
    agentType
    content
  }
}
```

### Get a specific AgentConfig with inline ID

```graphql
{
  agentConfigs(id:"d9c3991b-f206-4a30-bf46-6f0cf690bf04") {
    id
    agentType
    content
  }
}
```

### Get a specific AgentConfig with query variables

```graphql
query GetSpecificAgentConfig($id:String!)
{
  agentConfigs(id:$id) {
    id
    agentType
    content
  }
}
```

given 

```json
{
  "id": "d9c3991b-f206-4a30-bf46-6f0cf690bf04"
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
