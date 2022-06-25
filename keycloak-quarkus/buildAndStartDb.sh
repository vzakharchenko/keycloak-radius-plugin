set -e
./build.sh
rm -rf target/keycloak/keycloak-18.0.2/data
cp -r data target/keycloak/keycloak-18.0.2/data
./start.sh
