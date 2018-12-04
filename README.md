This module contains the API services that are part of Salus Telemetry. It currently includes
two Maven modules for the admin and public API, each with their own README. 
**NOTE** they are mainly separated due to the need for
different authentication means and distinct use cases addressed by each API.

## Building

The included modules can be built in one-shot using:

```bash
mvn package
```