set -e
./build.sh
rm -rf target/keycloak/keycloak-23.0.5/data
cp -r data target/keycloak/keycloak-23.0.5/data
./start.sh
