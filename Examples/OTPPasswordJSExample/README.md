# Radius OTP Password Example (Google Authenticator)

# Build and Start
1. build and run keycloak (select installation)
    1.1 [docker installation](../../docker)
    ```
          docker run -p 8090:8080 -p1812:1812/udp -p1813:1813/udp -e JAVA_OPTS="-Dkeycloak.profile.feature.scripts=enabled -Dkeycloak.profile.feature.upload_scripts=enabled -server -Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true" -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -v `pwd`/.:/example  -e KEYCLOAK_IMPORT=/example/Realm.json  vassio/keycloak-radius-plugin
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
2. open [http://localhost:8090/auth/]() and initialize master realm with login/password. ![initRealm](../../docs/importRealm2.png)
3. open Administration Console
4. import realm from file [Realm.json](Realm.json) ![importRealm](../../docs/importRealm.png)
5. add User test/test for realm otpPassword ![createUser](../../docs/createUser.png)![setPassword_1](../../docs/setPassword_1.png)
6. imporsonate user and logout ![impersonateUserExample](../../docs/impersonateUserExample.png) ![impersonateUserExample2](../../docs/impersonateUserExample2.png)
7. login to realm as user test and configure otp ![impersonateUserExample3](../../docs/impersonateUserExample3.png) ![impersonateUserExample4](../../docs/impersonateUserExample4.png)
8. install example
<pre>
cd Examples/OTPPasswordJSExample
npm i
node server.js
</pre>
7. open [http://localhost:3001/](http://localhost:3001/)
8. type login test<OTP Password> ![otpPasswordClient](../../docs/otpPasswordClient.png) ![otp](../../docs/otp.png)
9. click the "connect To Radius Server"

"SUCCESS"



