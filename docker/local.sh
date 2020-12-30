docker stop keycloak-radius-plugin
docker rm keycloak-radius-plugin
set -e
cp -r ../cli ./cli
docker build -t keycloak-radius-plugin .
docker run --env-file ./.example.radius.env -e KEYCLOAK_PASSWORD="admin" -e KEYCLOAK_USER="admin" -e  KEYCLOAK_IMPORT="/config/realm-example.json" -p 8080:8080 --name=keycloak-radius-plugin keycloak-radius-plugin
