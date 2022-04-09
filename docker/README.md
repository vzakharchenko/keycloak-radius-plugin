# run Keycloak radius inside Docker

## Install Docker

First, [install and run Docker](https://docs.docker.com/engine/install/) on your Linux server.

## Download

Get the trusted build from the [Docker Hub registry](https://hub.docker.com/r/vassio/keycloak-radius-plugin/):

```
docker pull vassio/keycloak-radius-plugin
```

## Download multiarch(amd64, arm64, arm/v7)

Get the trusted build from the [Docker Hub registry](https://hub.docker.com/r/vassio/keycloak-radius-plugin/):

```
docker pull vassio/keycloak-radius-plugin:latest-multiarch
```

## How to use this image

### Environment variables
   This Docker image uses the following variables, that can be declared in an env file ([example](./.example.radius.env):
```
RADIUS_SHARED_SECRET="secret"
RADIUS_UDP=true
RADIUS_UDP_AUTH_PORT=1812
RADIUS_UDP_ACCOUNT_PORT=1813
RADIUS_RADSEC=false
RADIUS_RADSEC_PRIVATEKEY="/config/private.key"
RADIUS_RADSEC_CERTIFICATE="/config/public.crt"
RADIUS_DICTIONARY=""
RADIUS_COA=false
RADIUS_COA_PORT="3799"
```

- **RADIUS_SHARED_SECRET** - Radius shared secret
- **RADIUS_UDP** - use Radius auth and Account
- **RADIUS_UDP_AUTH_PORT** - Auth port(if RADIUS_UDP = true)
- **RADIUS_UDP_ACCOUNT_PORT** - Accounting port(if RADIUS_UDP = true)
- **RADIUS_RADSEC** - use RadSec protocol
- **RADIUS_RADSEC_PRIVATEKEY** - rsa private key for Rad Sec
- **RADIUS_RADSEC_CERTIFICATE** - certificate for RadSec
- **RADIUS_COA** -send disconnect message if the keycloak session has expired
- **RADIUS_COA_PORT** - CoA port (Mikrotik:3799, Cisco:1700)
- **RADIUS_DICTIONARY** - path to the dictionary file in freeradius format

### [Keycloak Configuration](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md)

### Start the Keycloak Radius Server (dev mode)
Create a new Docker container from this image (replace `./radius.env` with your own `env` file):

```
docker run -d --name keycloak-radius-plugin --env-file .example.radius.env --restart=always -p 8080:8080 -p1812:1812/udp -p1813:1813/udp vassio/keycloak-radius-plugin start-dev
```
- arm64, arm/v7 version
```
docker run -d --name keycloak-radius-plugin --env-file .example.radius.env --restart=always -p 8080:8080 -p1812:1812/udp -p1813:1813/udp vassio/keycloak-radius-plugin:latest-multiarch start-dev
```

### Start the Keycloak Radius Server (production mode)
Create a new Docker container from this image (replace `./radius.env` with your own `env` file):

[maltegrosse comment](https://github.com/vzakharchenko/keycloak-radius-plugin/issues/542#issuecomment-1094090516)


## RUN Instance
###

### docker  compose
```
docker network create docker_default
docker-compose -f docker/docker-compose-keycloak.yaml create
docker-compose -f docker/docker-compose-keycloak.yaml start
```

## RadSec configuration

1. [generate private and Public Key](../keycloak-plugins/rad-sec-plugin/README.md)

2. [docker-compose-keycloak.yaml](docker-compose-keycloak.yaml):
```
RADIUS_RADSEC = 'true'
RADIUS_RADSEC_PRIVATEKEY = /config/private.key
RADIUS_RADSEC_CERTIFICATE = /config/public.crt
```

## Example Radius Realm

|                    |                      |
|:-------------------|:---------------------|
| Realm              | Radius-Realm-example |
| Radius Client Name | Radius               |
| User               | testUser             |
| Password           | testUser             |
1. login with testUser/testUser to [http://localhost:8090/auth/realms/Radius-Realm-example/protocol/openid-connect/auth?client_id=account&redirect_uri=http%3A%2F%2Flocalhost%3A8090%2Fauth&state=0&response_type=code&scope=openid
](http://localhost:8090/auth/realms/Radius-Realm-example/protocol/openid-connect/auth?client_id=account&redirect_uri=http%3A%2F%2Flocalhost%3A8090%2Fauth%2Frealms%2FRadius-Realm-example%2Faccount%2Flogin-redirect?path%3Dapplications&state=0%2F84406c9b-2682-4af3-b367-d9ae37e9a34f&response_type=code&scope=openid)
2. reset Radius Password

## Logging
```
docker logs keycloak-radius-plugin -f
```

## Bash shell inside container
To start a Bash session in the running container:
```
docker exec -it keycloak-radius-plugin bash
```

## Bash shell inside container
To start a Bash session in the running container:
```
docker exec -it keycloak-radius-plugin bash
```


## [build and Run locally](local.sh)

```
docker stop keycloak-radius-plugin
docker rm keycloak-radius-plugin
set -e
docker build -t keycloak-radius-plugin .
docker run --env-file ./example.radius.env -e KEYCLOAK_ADMIN_PASSWORD="admin" -e KEYCLOAK_ADMIN="admin" --name=keycloak-radius-plugin keycloak-radius-plugin  start-dev

```

## Deploy new release to dockerhub
```
./docker.publish.sh
```
