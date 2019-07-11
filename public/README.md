
This module contains the public/tenant facing APIs.

# Running locally, non-secure

In dev, the `unsecured` profile is enabled by default to bypass Repose/Keystone authentication.  The Public API application will run non-secure and bind to port 8080.

When using IntelliJ, you must add the `unsecured` profile to the Run Configuration, as that setting will override the application.yml."

# Repose-based authentication

In production, the the default configuration options can be used to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.