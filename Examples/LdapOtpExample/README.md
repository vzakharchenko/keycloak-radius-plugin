# LDAP Radius OTP Password Example (Google Authenticator)

# Build and Start
1. build and run keycloak, openldap and phpldapadmin
    ```
          docker-compose -f docker-compose.yaml up
    ```
2. add user account to openldap
    2.1 login http://localhost:8080/ with login: **cn=admin,dc=example,dc=org** and password=**admin** ![ldap1](../../docs/ldap1.png)
    2.2 add "Courier Mail: Account" ![ldap2](../../docs/ldap2.png) ![ldap2](../../docs/ldap3.png)
3. open [http://localhost:8090/auth/]
4. login **admin**/**admin**
5. sync LDAP Users ![ldap4](../../docs/ldap4.png) ![ldap5](../../docs/ldap5.png)
6. Configure OTP
 6.1 imporsonate user and logout ![ldap6](../../docs/ldap6.png)  ![ldap7](../../docs/ldap7.png)
 6.2 Click "Sign In"![ldap8](../../docs/ldap8.png)
7. install example
<pre>
cd Examples/LdapOtpExample
npm i
node server.js
</pre>
8. open [http://localhost:3001/](http://localhost:3001/)
9. type login **vaszakharchenko@gmail.com** password: **testOTP_Password** ![ldap9](../../docs/ldap9.png)
9. click the "connect To Radius Server"

"SUCCESS"



