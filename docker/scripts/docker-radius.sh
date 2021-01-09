set -e
export FILE=~/status
if [ -f "$FILE" ]; then
    echo "$FILE exist"
else
 echo "$FILE Created"
  mkdir ~/artifacts
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=radius-theme&e=zip&v=1.3.2" --output ~/artifacts/radius-theme.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=radius-plugin&e=jar&v=1.3.2" --output ~/artifacts/radius-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=radius-disconnect-plugin&e=jar&v=1.3.2" --output ~/artifacts/radius-disconnect-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=proxy-radius-plugin&e=jar&v=1.3.2" --output ~/artifacts/proxy-radius-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=rad-sec-plugin&e=jar&v=1.3.2" --output ~/artifacts/rad-sec-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=mikrotik-radius-plugin&e=jar&v=1.3.2" --output ~/artifacts/mikrotik-radius-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=cisco-radius-plugin&e=jar&v=1.3.2" --output ~/artifacts/cisco-radius-plugin.zip
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=chillispot-radius-plugin&e=jar&v=1.3.2" --output ~/artifacts/chillispot-radius-plugin.zip

echo "Radius Theme Installing..."
# Radius Theme
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.theme --resources=~/artifacts/radius-theme.zip"
echo "Core..."
# Core
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius --resources=~/artifacts/radius-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,javax.servlet.api,org.jboss.resteasy.resteasy-jaxrs,javax.ws.rs.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec"

echo "RadSec..."
# RadSec Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.rad.sec --resources=~/artifacts/rad-sec-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius"

echo "Mikrotik..."
# Mikrotik Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.mikrotik --resources=~/artifacts/mikrotik-radius-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius"

echo "Cisco..."
# Cisco Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.cisco --resources=~/artifacts/cisco-radius-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius"

echo "Chillispot..."
# Cisco Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.chillispot --resources=~/artifacts/chillispot-radius-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius"

echo "Disconnect..."
# Disconnect Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.dm --resources=~/artifacts/radius-disconnect-plugin.zip --dependencies=org.jboss.logging,org.keycloak.keycloak-core,javax.ws.rs.api,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius,org.keycloak.keycloak-model-jpa,javax.persistence.api,org.hibernate"

echo "Proxy..."
# Proxy Module
/opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=keycloak.plugins.radius.proxy --resources=~/artifacts/proxy-radius-plugin.zip  --dependencies=org.jboss.logging,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.apache.commons.io,javax.activation.api,com.fasterxml.jackson.core.jackson-databind,org.keycloak.keycloak-common,com.fasterxml.jackson.core.jackson-core,javax.transaction.api,org.hibernate,io.netty,org.slf4j,javax.xml.bind.api,org.apache.commons.codec,keycloak.plugins.radius"

echo "Install..."
# Install modules
/opt/jboss/keycloak/bin/jboss-cli.sh --file=/opt/radius/cli/radius.cli
# Install modules HA
echo "Install HA..."
/opt/jboss/keycloak/bin/jboss-cli.sh --file=/opt/radius/cli/radius-ha.cli
 echo "FIRST_START" > $FILE
fi
