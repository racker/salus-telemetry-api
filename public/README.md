
This module contains the public/tenant facing APIs.

## Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.

## Example GraphQL operations

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