docker stop keycloak-radius-plugin
docker rm keycloak-radius-plugin
set -e
docker build -t keycloak-radius-plugin .
docker run --env-file ./.example.radius.env -e KEYCLOAK_ADMIN_PASSWORD="admin" -e KEYCLOAK_ADMIN="admin"  -p 8080:8080 --name=keycloak-radius-plugin keycloak-radius-plugin start-dev
