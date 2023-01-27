set -e
./build.sh
rm -rf target/keycloak/keycloak-20.0.3/data
cp -r data target/keycloak/keycloak-20.0.3/data
./start.sh
