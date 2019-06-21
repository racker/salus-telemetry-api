
This module contains the public/tenant facing APIs.

# Running locally, non-secure

With the default configuration, the Public API application will run non-secure and bind to port 8080.

# Repose-based authentication

In production, the `secured` Spring profile needs to be activated to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.