
echo 'POSTGRES_DATABASE (default: keycloak_vassio)'
read POSTGRES_DATABASE
POSTGRES_DATABASE=${POSTGRES_DATABASE:-keycloak_vassio}

echo 'POSTGRES_USER(default: keycloak_vassio)'
read POSTGRES_USER
POSTGRES_USER=${POSTGRES_USER:-keycloak_vassio}

echo 'POSTGRES_PASSWORD(default: keycloak_vassio)'
read POSTGRES_PASSWORD
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-keycloak_vassio}

echo 'POSTGRES_PORT_5432_TCP_ADDR(default: 127.0.0.1)'
read POSTGRES_PORT_5432_TCP_ADDR
POSTGRES_PORT_5432_TCP_ADDR=${POSTGRES_PORT_5432_TCP_ADDR:-127.0.0.1}

echo "POSTGRES_DATABASE=${POSTGRES_DATABASE}" > keycloak.properties
echo "POSTGRES_USER=${POSTGRES_USER}" >> keycloak.properties
echo "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" >> keycloak.properties
echo "POSTGRES_PORT_5432_TCP_ADDR=${POSTGRES_PORT_5432_TCP_ADDR}" >> keycloak.properties

rm -f keycloak_source.zip
wget https://downloads.jboss.org/keycloak/8.0.1/keycloak-8.0.1.zip -O keycloak_source.zip  -q --show-progress --progress=bar:force 2>&1
