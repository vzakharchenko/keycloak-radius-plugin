# Radius OneTime Password Example (OIDC)
 - authorize to radius using OIDC connection.
 - Password sync with access token(if token expired then pasword also expired)
 - during refresh token, password will be updated.
 - password can be used only once

# Build and Start
1. build and run keycloak (select one of installation)
    1.1 [docker installation](../../docker)
    ```
        docker run -p 8090:8080 -p 8190:8190 -p1812:1812/udp -p1813:1813/udp   -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -e DEBUG=true -e DEBUG_PORT=*:8190  -v `pwd`/.:/example  -e KEYCLOAK_ADMIN_IMPORT=/example/Realm.json  vassio/keycloak-radius-plugin start-dev
    ```
    1.2 release installation
        <pre>
        - download and unzip keycloak-radius.zip (https://github.com/vzakharchenko/keycloak-radius-plugin/releases)
        - unzip keycloak-radius.zip -d keycloak-radius
        - cd keycloak-radius
        - sh bin/standalone.sh   -Dkeycloak.profile.feature.upload_scripts=enabled  -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --debug 8190 -Djboss.http.port=8090
        </pre>
    1.3 Develop  installation
    ```sh
    sudo apt-get install net-tools # Only once
    cd keycloak
    ./init.sh # Only once
    ./buildAndStart.sh
    ```
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



