set -e
noquotes() { printf '%s' "$1" | sed -e 's/^"\(.*\)"$/\1/' -e "s/^'\(.*\)'$/\1/"; }

RADIUS_SHARED_SECRET=$(noquotes "$RADIUS_SHARED_SECRET")
RADIUS_UDP_AUTH_PORT=$(noquotes "$RADIUS_UDP_AUTH_PORT")
RADIUS_UDP_ACCOUNT_PORT=$(noquotes "$RADIUS_UDP_ACCOUNT_PORT")
RADIUS_UDP=$(noquotes "$RADIUS_UDP")
RADIUS_RADSEC_PRIVATEKEY=$(noquotes "$RADIUS_RADSEC_PRIVATEKEY")
RADIUS_RADSEC_CERTIFICATE=$(noquotes "$RADIUS_RADSEC_CERTIFICATE")
RADIUS_RADSEC=$(noquotes "$RADIUS_RADSEC")
RADIUS_COA_PORT=$(noquotes "$RADIUS_COA_PORT")
RADIUS_COA=$(noquotes "$RADIUS_COA")


if [ -z "$RADIUS_SHARED_SECRET" ]; then
  echo "Radius Shared secret is empty."
  exit 1;
fi

configJSON="{\"sharedSecret\":\"$RADIUS_SHARED_SECRET\",\"authPort\":$RADIUS_UDP_AUTH_PORT,\"accountPort\":$RADIUS_UDP_ACCOUNT_PORT,\"useUdpRadius\":$RADIUS_UDP,\"externalDictionary\":\"$RADIUS_DICTIONARY\",\"radsec\":{\"privateKey\":\"$RADIUS_RADSEC_PRIVATEKEY\",\"certificate\":\"$RADIUS_RADSEC_CERTIFICATE\",\"useRadSec\":$RADIUS_RADSEC},\"coa\":{\"port\":$RADIUS_COA_PORT,\"useCoA\":$RADIUS_COA}}"
echo "$configJSON"
mkdir -v -p /opt/keycloak/config/
echo "$configJSON">/opt/keycloak/config/radius.config
