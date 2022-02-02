set -e
./build.sh
rm -rf target/keycloak/keycloak-16.1.1/standalone/data
cp -r data target/keycloak/keycloak-16.1.1/standalone/data
./start.sh
