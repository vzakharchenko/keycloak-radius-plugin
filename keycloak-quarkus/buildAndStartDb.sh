set -e
./build.sh
rm -rf target/keycloak/keycloak-23.0.4/data
cp -r data target/keycloak/keycloak-23.0.4/data
./start.sh
