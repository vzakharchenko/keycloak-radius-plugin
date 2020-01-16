# How Keycloak Radius Hotspot works
1. Authorization through Keycloak occurs by [OpenID Connect](https://www.keycloak.org/docs/latest/securing_apps/#openid-connect-2).
2. User selects on the login page the identity provider through which he wants to log in
3. The result of a successful authorization is a JWT that contains a temporary session key.
4. With this key, the User is authorized through Radius Server.
5. Radius Server checks if this key is in the user session. And whether it was used.
6. Radius Server successfully authorizing the user

# connection Schema`s

## Cloud connection (**Use Radsec is required!!!** )
![KeycloakRadius (1)](../docs/KeycloakRadius%20%281%29.png)


##  Proxy connection
![KeycloakRadius](../docs/KeycloakRadius2%20%281%29.png)

# Setup, build and configure  HotSpot page for Social Login

1. Create Realm ![hotspotRealm](../docs/hotspotRealm.png)
2. create Radius Client ![RadiusClientHotSpot](../docs/RadiusClientHotSpot.png)
3. create OpenId client ![hotspotClient](../docs/hotspotClient.png)
4. Setting your Hotspot DNS in "Valid Redirect URIs" and "Web Origins" ![HotspotClientConfiguration](../docs/HotspotClientConfiguration.png)
5. add "Radius Session Password" Mapper  ![HotSpotMapper](../docs/HotSpotMapper.png)![HotSpotMapper2](../docs/HotSpotMapper2_1.png)
6. Download keycloak.json ![downloadKeycloakJson](../docs/downloadKeycloakJson.png)

##  Setup Mikrotik
1. Upload all files from [hotspot/mikrotik](mikrotik) to flash/hotspot on device ([authorization.js](mikrotik/authorization.js) and [login.html](mikrotik/login.html))
-  Using web UI
-  Using scp
- Using ftp
- Using winbox
2. Download keycloak.json ![downloadKeycloakJson](../docs/downloadKeycloakJson.png)
3. upload keycloak.json into flash/hotspot on device
4. update Walled Garden. Add your keycloak host ![addWalledGarden](../docs/addWalledGarden.png) ![KeycloakHostName](../docs/KeycloakHostName.png)

## Facebook Login example
1.  Download Keycloak 8.0.1
2.  install [ngrok](https://ngrok.com/). Register ngrok  <pre><code>./ngrok authtoken \<YOUR TOKEN\></pre></code>
3.  [Configure Keycloak](../README.md#configure-keycloak)
4.  [Start Keycloak](../README.md#run-keycloak-locally)
5. start ngrok <pre><code>./ngrok http 8090</pre></code>![Ngrok](../docs/Ngrok.png)
6. open keycloak goto realm and add Facebook Identity Provider ![SelectFacbook](../docs/SelectFacbook.png)
7. Copy Redirect URI ![Copy Redirect URI](../docs/Copy%20Redirect%20URI.png)
8. goto [https://developers.facebook.com/](https://developers.facebook.com/) and create a new application ![CreateApp1](../docs/CreateApp1.png)![CreateApp2](../docs/CreateApp2.png)![FacebookLogin3](../docs/FacebookLogin3.png)![Facebook4](../docs/Facebook4.png)
9. Insert Redirect URI from [Step 7](#L43) ![Facebook5](../docs/Facebook5.png)
10. Get App Id and Secret from application (Settings->basic) ![Facebook6](../docs/Facebook6.png)
11. back to Keycloak and set this App Id and Secret ![Facebook7](../docs/Facebook7.png)
12. add facebook hosts to Walled Garden ![FacebookWalledGarden](../docs/FacebookWalledGarden.png)
<pre><code>/ip hotspot walled-garden
add comment=facebook dst-host=facebook.*
add comment=facebook dst-host=*.facebook.*
add comment=facebook dst-host=*.fbcdn.*
add comment=facebook dst-host=*akamai*
add comment=facebook dst-host=*atdmt*
add comment=facebook dst-host=*fbsbx*
add comment=common dst-host=www.google-analytics.com
</pre></code>

13. open hotspot page ![FacebookLoginHotspot](../docs/FacebookLoginHotspot.png) ![FacebookLogin2](../docs/FacebookLogin2.png)



## build UI

### build UI Requirements:
node and npm must be installed  
macbook instalation [brew](https://brew.sh/) : *brew install node*  
[Install node on ubuntu ](https://linuxize.com/post/how-to-install-node-js-on-ubuntu-18.04/)

### Building steps
1. cd hotsot_ui
2. npm i
3. npm run build  
result in [./hotspot](../hotspot)

