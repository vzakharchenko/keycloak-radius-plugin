#!/usr/bin/env bash
set -e
cd target/keycloak/keycloak-15.0.1
sh bin/standalone.sh -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 -Dkeycloak.profile.feature.upload_scripts=enabled --debug 8190 -Djboss.http.port=8090
