# Keycloak Radius Integration
[![CircleCI](https://circleci.com/gh/vzakharchenko/keycloak-radius-plugin/tree/master.svg?style=svg)](https://circleci.com/gh/vzakharchenko/keycloak-radius-plugin/tree/master)  
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/vzakharchenko/keycloak-radius-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/vzakharchenko/keycloak-radius-plugin/context:java)  
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bfe4a9d54c5048d5b4c05ba6a4cb9b96)](https://www.codacy.com/manual/vzaharchenko/keycloak-radius-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=vzakharchenko/keycloak-radius-plugin&amp;utm_campaign=Badge_Grade)  
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/keycloak-radius-plugin/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/keycloak-radius-plugin?branch=master)  
<a href="https://codeclimate.com/github/vzakharchenko/keycloak-radius-plugin/maintainability"><img src="https://api.codeclimate.com/v1/badges/499d56ae9242cfaf2cbb/maintainability" /></a>

Run radius server inside keycloak.  
features:
- radius server inside keycloak
- support radsec (Radius over TLS)
- hotspot:
  - pop,chap authorization
  - openID connect
  - login using facebook, google, etc...

support Mikrotik services: hotspot, login
## setup
### build project
***requirements***: java jdk 11 and above, maven 3.5 and above
 - <pre><code>cd keycloak-plugins</pre></code>
 - <pre><code>mvn clean install</pre></code>
### Configure keycloak
***requirements***: [keycloak 8.0.1](https://downloads.jboss.org/keycloak/8.0.1/keycloak-8.0.1.zip) (I think it should work on earlier versions)
- setup radius-plugin <pre><code>${KEYCLOAK_PATH}/bin/jboss-cli.sh --command=module add --name=keycloak.plugins.radius --resources=${SOURCE}/keycloak-plugins/radius-plugin/target/radius-plugin-1.0-SNAPSHOT.jar --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,javax.servlet.api,org.jboss.resteasy.resteasy-jaxrs,javax.ws.rs.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec</pre></code>
- setup rad-sec plugin <pre><code>${KEYCLOAK_PATH}/bin/jboss-cli.sh --command=module add --name=keycloak.plugins.rad.sec --resources=${SOURCE}/keycloak-plugins/rad-sec-plugin/target/rad-sec-plugin-1.0-SNAPSHOT.jar --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius</pre></code>
- setup mikrotik plugin <pre><code>${KEYCLOAK_PATH}/bin/jboss-cli.sh --command=module add --name=keycloak.plugins.radius.mikrotik --resources=${SOURCE}/keycloak-plugins/mikrotik-radius-plugin/target/mikrotik-radius-plugin-1.0-SNAPSHOT.jar --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius</pre></code>
- run script for standalone <pre><code>${KEYCLOAK_PATH}/bin/jboss-cli.sh --file=${SOURCE}/cli/radius.cli</pre></code>
- run script for standalone-ha <pre><code>${KEYCLOAK_PATH}/bin/jboss-cli.sh --file=${SOURCE}/cli/radius-ha.cli</pre></code>
where
- **KEYCLOAK_PATH** - Path where you are unpacked keycloak-8.0.1.zip
- **SOURCE** - Path where you checked out the code and built the project
## Configuration
### Radius server config file
-  create file ${KEYCLOAK_PATH}config/radius.config
-  example <pre><code>{
  {
   "sharedSecret":"radsec",
   "authPort":1812,
   "accountPort":1813,
   "useUdpRadius":false,
   "radsec":{
      "privateKey":"config/private.key",
      "certificate":"config/public.crt",
      "useRadSec":true
   }
}</code></pre>
where
   -  **sharedSecret** - Used to secure communication between a RADIUS server and a RADIUS client.
   -  **authPort** - Authentication and authorization port
   -  **accountPort** - Accounting port
   -  **useUdpRadius** - if true, then listen to authPort and accountPort
   -  **radsec** - radsec configuration
   -  **privateKey** - private SSL key (https://netty.io/wiki/sslcontextbuilder-and-private-key.html)
   -  **certificate** - certificates chain
   -  **useRadSec** - if true, then listen  radsec port
## Run Keycloak Locally
<pre><code>
#!/usr/bin/env bash
set -e
cd target/keycloak/keycloak-8.0.1
public_ip=`ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1' -m 1`
sh bin/standalone.sh  -c standalone-ha.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address.private=${public_ip} -Djboss.node.name=${public_ip} -Djgroups.bind.address=${public_ip} --debug 8190 -Djboss.http.port=8090
</pre></code>

### Keycloak Client with Radius Protocol
![radiusProtocol](docs/radiusProtocol.png)

### Assign Radius Attributes to Role
> **_NOTE:_**  Composite roles supported

![RoleAttributes](docs/RoleAttributes.png)
### Assign Radius Attributes to Group
> **_NOTE:_**  SubGroups supported
![groupAttributes](docs/groupAttributes.png)
### Assign Radius Attributes to User
![userAttributes](docs/userAttributes.png)

### Mikrotik Login Example (Radius Server)
1. create ${KEYCLOAK_PATH}/config/radius.config
<pre><code>{
  {
   "sharedSecret":"TEST",
   "authPort":1812,
   "accountPort":1813,
   "useUdpRadius":true,
   "radsec":{
      "privateKey":"config/private.key",
      "certificate":"config/public.crt",
      "useRadSec":false
   }
}</code></pre>

2.  create "mikrotik_login" Realm![createRealm](docs/createRealm.png)
3. create "radius" client ![radiusClient](docs/radiusClient.png)
4. create role "MIKROTIK-ADMIN" ![createAdminRole](docs/createAdminRole.png)
5. assign radius attribute "Mikrotik-Group"="full" to Role "MIKROTIK-ADMIN" ![addAttribute](docs/addAttribute.png)
6. create "testUser" User ![addTestUser](docs/addTestUser.png)
7. set Password "test" for User. uncheck "Temporary"  ![SetPassword](docs/SetPassword.png)
8. assign Role "MIKROTIK-ADMIN" to "testUser" ![AssignRole](docs/AssignRole.png)
9. set Action "Update Radius Password" (or send this event to user be email) ![updateRadiusPassword](docs/updateRadiusPassword.png)
10. Impersonate user ![Impersonate](docs/Impersonate.png)
11. Sign-out ![SignOut](docs/SignOut.png)
12. Login with testUser:test ![loginNewUser](docs/loginNewUser.png)
13. Set Radius User Password ![RadiusUserPassword](docs/RadiusUserPassword.png)
14. open Mikrotik Radius configuration Page ![RadiusSetting](docs/RadiusSetting.png)
15. enable Radius AAA ![AAA](docs/AAA.png) ![useRadiusUsers](docs/useRadiusUsers.png)
16. try to login with a new User ![loginMikrotik](docs/loginMikrotik.png) ![webActive](docs/webActive.png)
17. try to login by ssh(the same for telnet and winbox) ![sshAccess](docs/sshAccess.png)![sshActive](docs/sshActive.png)

### Mikrotik Rad-sec Example
1. generate keys and certificates (**I don't have experience with openssl, so I am using [https://certificatetools.com/](https://certificatetools.com/)** )
2. Page 1 ![CerificatePage1](docs/CerificatePage1.png)
3. Page 2 ![CertificatePage2](docs/CertificatePage2.png)
4. download private key to ${KEYCLOAK_PATH}/config/private.key
5. download PEM Certificate to ${KEYCLOAK_PATH}/config/public.crt
6. enable radSec( [sharedSecret have to be "radsec"](https://wiki.mikrotik.com/wiki/Manual:RADIUS_Client) ) :
<pre><code>{
  {
   "sharedSecret":"redsec",
   "authPort":1812,
   "accountPort":1813,
   "useUdpRadius":true,
   "radsec":{
      "privateKey":"config/private.key",
      "certificate":"config/public.crt",
      "useRadSec":true
   }
}</code></pre>
![RadSecWarning](docs/RadSecWarning.png)

7. download PKCS#12 Certificate
8. upload PKCS#12 Certificate  to Mikrotik ![uploadCertificate](docs/uploadCertificate.png)
9. import Certificate ![import Certificate](docs/import%20Certificate.png)
10. enable radsec ![radiusRadSec](docs/radiusRadSec.png)

### Mikrotik Hotspot Example (with Facebook login)

in progress...
