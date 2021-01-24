set -e
./build.sh
rm -rf target/keycloak/keycloak-12.0.2/standalone/data
cp -r data target/keycloak/keycloak-12.0.2/standalone/data
./start.sh
