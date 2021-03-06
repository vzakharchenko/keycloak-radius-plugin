# Radius OneTime Password Example (OIDC)
 - authorize to radius using OIDC connection.
 - Password sync with access token(if token expired then pasword also expired)
 - during refresh token, password will be updated.
 - password can be used only once

Build and Start
1. build and run keycloak (docker https://github.com/vzakharchenko/keycloak-radius-plugin/tree/master/docker)
<pre>
 - download and unzip keycloak-radius.zip (https://github.com/vzakharchenko/keycloak-radius-plugin/releases)
 - unzip keycloak-radius.zip -d keycloak-radius
 - cd keycloak-radius
 - sh bin/standalone.sh  -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --debug 8190 -Djboss.http.port=8090
 - open http://localhost:8090
</pre>
2. open [http://localhost:8090/auth/]() and initialize master realm with login/password. ![initRealm](../../docs/initRealm.png)
3. open Administration Console
4. import realm from file [Realm.json](Realm.json) ![importRealm](../../docs/importRealm.png)
5. add User test/test for realm oneTimePassword ![createUser](../../docs/createUser.png)![setPassword_1](../../docs/setPassword_1.png)
6. install example
<pre>
cd Examples/OneTimePasswordJSExample
npm i
node server.js
</pre>
7. open [http://localhost:3000/](http://localhost:3000/)
8. type login and password(test/test)
9. click the "connect To Radius Server" ![testOneTimePassword](../../docs/testOneTimePassword.png)
- Press Once - "SUCCESS"
- all other clicks - "REJECT"
- If you wait 5 mins -  "REJECT"



