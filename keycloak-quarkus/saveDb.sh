#!/usr/bin/env bash
set -e

kc_ver='24.0.4'
kc_dir="target/keycloak/keycloak-$kc_ver"

cd ../keycloak-quarkus

mv data data.$(date +%Y%m%d-%H%M%S)
mkdir data
cp -r "${kc_dir}/data/h2" data/h2
cp -r "${kc_dir}/conf/cache-ispn.xml" data/
cp -r "${kc_dir}/conf/keycloak.conf" data/
cp -r "${kc_dir}/config/radius.config" data/