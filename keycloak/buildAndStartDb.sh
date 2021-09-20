set -e
./build.sh
rm -rf target/keycloak/keycloak-15.0.2/standalone/data
cp -r data target/keycloak/keycloak-15.0.2/standalone/data
./start.sh
