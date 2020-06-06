#!/bin/bash
/opt/radius/scripts/keycloak.sh -Dkeycloak.profile.feature.upload_scripts=enabled --debug 8190 2>&1
/opt/jboss/tools/docker-entrypoint.sh