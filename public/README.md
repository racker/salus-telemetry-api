
This module contains the public/tenant facing APIs.

# Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.

# Example GraphQL operations

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
    "content": "testing",
    "labels": [
      {
        "name": "os",
        "value": "darwin"
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
    identifier
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
    "identifier": "myid",
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
    identifier
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
Must specify the identifier and value.

```graphql
mutation DeleteResource ($i:String!, $iv:String!) {
  deleteResource(identifier:$i, identifierValue:$iv) {
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