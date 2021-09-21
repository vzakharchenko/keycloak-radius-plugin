rm -rf ./cli
cp -r ../cli ./cli

docker build -t keycloak-radius-plugin .
docker tag  keycloak-radius-plugin vassio/keycloak-radius-plugin:1.3.13
docker push vassio/keycloak-radius-plugin:1.3.13

docker tag  keycloak-radius-plugin vassio/keycloak-radius-plugin:latest
docker push vassio/keycloak-radius-plugin:latest
