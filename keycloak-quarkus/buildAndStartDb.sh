set -e
./build.sh
rm -rf target/keycloak/keycloak-21.0.0/data
cp -r data target/keycloak/keycloak-21.0.0/data
./start.sh
