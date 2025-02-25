#!/usr/bin/env bash
set -e

cd ../keycloak-plugins
mvn clean install -DskipTests -Dfast-build

cd ../keycloak
mvn clean install