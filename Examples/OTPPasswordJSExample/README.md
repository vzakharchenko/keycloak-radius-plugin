# Radius OTP Password Example (Google Authenticator)

Build and Start
1. build and run keycloak (docker https://github.com/vzakharchenko/keycloak-radius-plugin/tree/master/docker)
<pre>
- download and unzip keycloak-radius.zip (https://github.com/vzakharchenko/keycloak-radius-plugin/releases)
- unzip keycloak-radius.zip -d keycloak-radius
- cd keycloak-radius
- sh bin/standalone.sh  -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --debug 8190 -Djboss.http.port=8090
</pre>
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
8. type login test<OTP Password> and otp ![otpPasswordClient](../../docs/otpPasswordClient.png) ![otp](../../docs/otp.png)
9. click the "connect To Radius Server"

"SUCCESS"



