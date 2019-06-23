
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

# TEMPORARY : Agent releases and installation management

## Agent releases

### Get all
```http request
GET localhost:8888/api/agent-releases
```

### Get one
```http request
GET localhost:8888/api/agent-releases/:release-id
```

### Declare agent release

```http request
POST localhost:8888/api/agent-releases
{
  "type": "TELEGRAF",
  "version": "1.11.0",
  "labels": {
    "agent_discovered_os": "darwin",
    "agent_discovered_arch": "amd64"
  },
  "url": "https://homebrew.bintray.com/bottles/telegraf-1.11.0.high_sierra.bottle.tar.gz",
  "exe": "telegraf/1.11.0/bin/telegraf"
}
```

## Agent installs

### Get all for tenant
```http request
GET localhost:8888/api/agent-installs
```

### Install agent for tenant
```http request
POST localhost:8888/api/agent-installs
{
  "agentReleaseId": "$agentReleaseId",
  "labels": {
    "agent_discovered_os": "darwin"
  }
}
```