#  RadSec(Radius over TLS) Plugin for Keycloak
- secured communication between RADIUS/TCP peers on the transport layer
### Radsec Example
1. generate keys and certificates (**I don't have experience with openssl, so I am using [https://certificatetools.com/](https://certificatetools.com/)** )
2. Page 1 ![CerificatePage1](../../docs/CerificatePage1.png)
3. Page 2 ![CertificatePage2](../../docs/CertificatePage2.png)
4. download private key to ${KEYCLOAK_PATH}/config/private.key
5. download PEM Certificate to ${KEYCLOAK_PATH}/config/public.crt
6. enable radSec( [Mikrotik sharedSecret have to be "radsec"](https://wiki.mikrotik.com/wiki/Manual:RADIUS_Client) ) :
<pre><code>{
  {
   "sharedSecret":"radsec",
   "authPort":1812,
   "accountPort":1813,
   "useUdpRadius":true,
   "radsec":{
      "privateKey":"config/private.key",
      "certificate":"config/public.crt",
      "useRadSec":true
   }
}</code></pre>
![RadSecWarning](../../docs/RadSecWarning.png)

7. download PKCS#12 Certificate
8. upload PKCS#12 Certificate  to Mikrotik ![uploadCertificate](../../docs/uploadCertificate.png)
9. import Certificate ![import Certificate](../../docs/import%20Certificate.png)
10. enable radsec ![radiusRadSec](../../docs/radiusRadSec.png)
