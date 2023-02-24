set -e
export FILE=~/status
if [ -f "$FILE" ]; then
    echo "$FILE exist"
else
 echo "$FILE Created"
echo "Core..."
# Core
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=radius-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/radius-plugin.jar
echo "Disconnect..."
# Disconnect Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=radius-disconnect-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/radius-disconnect-plugin.jar
echo "Proxy..."
# Proxy Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=proxy-radius-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/proxy-radius-plugin.jar
echo "RadSec..."
# RadSec Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=rad-sec-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/rad-sec-plugin.jar
echo "Mikrotik..."
# Mikrotik Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=mikrotik-radius-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/mikrotik-radius-plugin.jar
echo "Cisco..."
# Cisco Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=cisco-radius-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/cisco-radius-plugin.jar
echo "Chillispot..."
# Cisco Module
  curl -J -L  "https://repository.sonatype.org/service/local/artifact/maven/content?r=central-proxy&g=com.github.vzakharchenko&a=chillispot-radius-plugin&e=jar&v=1.4.11" --output /opt/keycloak/providers/chillispot-radius-plugin.jar

echo "FIRST_START" > $FILE
fi
