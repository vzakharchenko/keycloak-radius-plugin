#!/bin/bash
/opt/radius/scripts/docker-radius.sh
/opt/radius/scripts/radius-keycloak.sh
/opt/keycloak/bin/kc.sh $*
