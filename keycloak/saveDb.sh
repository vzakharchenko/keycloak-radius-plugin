#!/usr/bin/env bash
set -e

kc_ver='26.2.5'
kc_dir="target/keycloak/keycloak-$kc_ver"
timestamp="$(date +%Y%m%d-%H%M%S)"

cd ../keycloak

mv data "data.$timestamp"
mkdir -p data/providers
cp -r "${kc_dir}/data/h2" data/h2
cp -r "${kc_dir}/conf/cache-ispn.xml" data/
cp -r "${kc_dir}/conf/keycloak.conf" data/
cp -r "${kc_dir}/config/radius.config" data/

# save additional plugins and themes
find "${kc_dir}/providers" -type f -name "*.jar" \! \( \
       -name "cisco-radius-plugin-*.jar" \
    -o -name "mikrotik-radius-plugin-*.jar" \
    -o -name "chillispot-radius-plugin-*.jar" \
    -o -name "rad-sec-plugin-*.jar" \
    -o -name "radius-disconnect-plugin-*.jar" \
    -o -name "proxy-radius-plugin-*.jar" \
    -o -name "radius-plugin-*.jar" \
  \) -exec cp -v "{}" data/providers/ \;