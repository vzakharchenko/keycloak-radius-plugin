#!/usr/bin/env bash
set -e

kc_ver='25.0.4'
kc_dir="target/keycloak/keycloak-$kc_ver"
timestamp="$(date +%Y%m%d-%H%M%S)"

cd ../keycloak-quarkus

mv data "data.$timestamp"
mkdir -p data/providers
cp -r "${kc_dir}/data/h2" data/h2
cp -r "${kc_dir}/conf/cache-ispn.xml" data/
cp -r "${kc_dir}/conf/keycloak.conf" data/
cp -r "${kc_dir}/config/radius.config" data/

# save additional plugins and themes
find "${kc_dir}/providers" -type f -name "*.jar" \! \( \
       -name "cisco-radius-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "mikrotik-radius-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "chillispot-radius-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "rad-sec-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "radius-disconnect-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "proxy-radius-plugin-1.5.1-SNAPSHOT.jar" \
    -o -name "radius-plugin-1.5.1-SNAPSHOT.jar" \
  \) -exec cp -v "{}" data/providers/ \;