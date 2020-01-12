# keycloak-radius-plugin

[![CircleCI](https://circleci.com/gh/vzakharchenko/keycloak-radius-plugin/tree/master.svg?style=svg)](https://circleci.com/gh/vzakharchenko/keycloak-radius-plugin/tree/master)  
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/vzakharchenko/keycloak-radius-plugin.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/vzakharchenko/keycloak-radius-plugin/context:java)  
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/keycloak-radius-plugin/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/keycloak-radius-plugin?branch=master)  
<a href="https://codeclimate.com/github/vzakharchenko/keycloak-radius-plugin/maintainability"><img src="https://api.codeclimate.com/v1/badges/499d56ae9242cfaf2cbb/maintainability" /></a>

features:

- hotspot:
  - pop,chap authorization
  - openID connect
  - login using facebook, google, etc...
- login
  - support api, winbox, web(Mikrotik)

## Configuration
### Radius server config file
-  create file \<Keycloak\>/config/radius.config
-  example <pre><code>{
  "provider" : "radius-provider",
  "sharedSecret" : "radius sharedSecret",
  "authPort" : 1812,
  "accountPort" : 1813,
  "radiusIpAccess" : [ {
    "ip" : "193.138.164.214",
    "sharedSecret" : "Shared Secret for Ip"
  } ],
  "useRadius" : true
}</code></pre>

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
