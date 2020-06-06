# Radius REJECT_ example

Build and Start
1. build and run keycloak  
    1.1 [docker](../../docker)  
    1.2 release  
        <pre>
        - download and unzip keycloak-radius.zip (https://github.com/vzakharchenko/keycloak-radius-plugin/releases)
        - unzip keycloak-radius.zip -d keycloak-radius
        - cd keycloak-radius
        - sh bin/standalone.sh   -Dkeycloak.profile.feature.upload_scripts=enabled  -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --debug 8190 -Djboss.http.port=8090
        </pre>  
    1.3 Develop  
    ```sh
    sudo apt-get install net-tools # Only once
    cd keycloak
    ./init.sh # Only once
    ./buildAndStart.sh
    ```
2. open [http://localhost:8090/auth/]() and initialize master realm with login/password. ![initRealm](../../docs/initRealm.png)  
3. open Administration Console  
4. import realm from file [Realm.json](Realm.json) ![importRealm](../../docs/ImportRealm3.png)  

|     User    |     password    |      ROLE        |
|:------------|:----------------|:-----------------|
| rejectUser  | rejectUser      | REJECT_ROLE      |
| acceptUser  | acceptUser      | ACCEPT_ROLE      |

|     ROLE     |               ATTRIBUTES                    |
|:-------------|:--------------------------------------------|
| REJECT_ROLE  | REJECT_NAS-IP-Address = 192.168.88.1        |
| ACCEPT_ROLE  | ACCEPT_NAS-IP-Address = 192.168.88.1        |

5. install example  
<pre>
cd Examples/ConditionAccessRequestJSExample
npm i
npm run start
</pre>
6. open [http://localhost:3001/](http://localhost:3001/)  
7. type login and password  
8. click the "connect To Radius Server" 

|     User    |     password    |   NAS-IP-Address      | Test Status |
|:------------|:----------------|:----------------------|:------------|
| rejectUser  | rejectUser      | 192.168.88.1          | REJECT      |
| rejectUser  | rejectUser      | 192.168.88.2          | ACCEPT      |
| acceptUser  | acceptUser      | 192.168.88.1          | ACCEPT      |
| acceptUser  | acceptUser      | 192.168.88.2          | REJECT      |



