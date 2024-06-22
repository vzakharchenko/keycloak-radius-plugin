#!/usr/bin/env bash
set -e

kc_ver='25.0.1'
kc_url="https://github.com/keycloak/keycloak/releases/download/$kc_ver/keycloak-$kc_ver.zip"
kc_zip='keycloak_source.zip'

cd ../keycloak-quarkus

rm -f "$kc_zip"
wget "$kc_url" -O "$kc_zip" -q --show-progress --progress=bar:force 2>&1