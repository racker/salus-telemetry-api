
This module contains the public/tenant facing APIs.

# Running locally, non-secure

In dev, the `unsecured` profile is enabled by default to bypass Repose/Keystone authentication.  The Public API application will run non-secure and bind to port 8080.

# Repose-based authentication

In dev, to utilize Repose you will need to remove the `unsecured` profile from application-dev.yml.

In production, the the default configuration options can be used to enable the processing
of [Repose KeystoneV2](https://repose.atlassian.net/wiki/spaces/REPOSE/pages/34275336/Keystone+v2+filter) supplied headers.