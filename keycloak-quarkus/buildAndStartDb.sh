#!/usr/bin/env bash
set -e

kc_ver='24.0.4'
kc_dir="target/keycloak/keycloak-$kc_ver"

cd ../keycloak-quarkus

./build.sh

rm -rf "${kc_dir}/data"
mkdir -p "${kc_dir}/data"
cp -r data/h2 "${kc_dir}/data/"

rm -rf "${kc_dir}/conf/keycloak.conf"
cp data/keycloak.conf "${kc_dir}/conf"

rm -rf "${kc_dir}/conf/cache-ispn.xml"
cp data/cache-ispn.xml "${kc_dir}/conf"

rm -rf "${kc_dir}/config/radius.config"
cp data/radius.config "${kc_dir}/config/radius.config"

./start.sh