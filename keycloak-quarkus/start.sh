#!/usr/bin/env bash
set -e
cd target/keycloak/keycloak-18.0.2
bin/kc.sh --debug 8190 start-dev --http-port=8090
