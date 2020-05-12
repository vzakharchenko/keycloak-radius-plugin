# run Keycloak radius inside Docker

## RUN Instance
```
docker-compose -f docker/docker-compose-keycloak.yaml create
docker-compose -f docker/docker-compose-keycloak.yaml start
```
## List of Parameters

  - **RADIUS_SHARED_SECRET** - Radius shared secret
  - **RADIUS_UDP** - use Radius auth and Account
  - **RADIUS_UDP_AUTH_PORT** - Auth port(if RADIUS_UDP = true)
  - **RADIUS_UDP_ACCOUNT_PORT** - Accounting port(if RADIUS_UDP = true)
  - **RADIUS_RADSEC** - use RadSec protocol
  - **RADIUS_RADSEC_PRIVATEKEY** - rsa private key for RadSec
  - **RADIUS_RADSEC_CERTIFICATE** - certificate for RadSec
  - **RADIUS_COA** -  send disconnect message if the keycloak session has expired 
  - **RADIUS_COA_PORT** - CoA port (Mikrotik:3799, Cisco:1700)

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
