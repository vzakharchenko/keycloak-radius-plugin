set -e

function help() {
  echo '
Usage release.sh OPTIONS
create release
Options:
       --help                         Help screen
       --password <pgp password>      pgp password
'
}

POSITIONAL=()
while [[ $# -gt 0 ]]; do
  key="$1"

  case $key in
  --password)
    password="$2"
    shift
    shift
    ;;
  --help)
    help
    exit
    ;;
  *) # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift              # past argument
    ;;
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

if [[ "x${password}" == "x" ]]; then
  echo "Type pgp password:"
  read password
fi

if [[ "x${password}" == "x" ]]; then
  echo "Password is empty"
  exit 1;
fi

cd keycloak-plugins
mvn clean release:prepare -Psign -Darguments=-Dgpg.passphrase=${password} -Dresume=false
mvn -Psign clean release:perform -Darguments=-Dgpg.passphrase=${password}