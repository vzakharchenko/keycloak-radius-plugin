set -e
./build.sh
rm -rf target/keycloak/keycloak-22.0.3/data
cp -r data target/keycloak/keycloak-22.0.3/data
./start.sh
