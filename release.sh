#!/bin/bash
set -e

PROPERTY_FILE=./keycloak-plugins/release.properties

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

# prepare release
cd keycloak-plugins
mvn clean release:prepare -Psign -Darguments=-Dgpg.passphrase=${password} -Dresume=false -DskipTests
cd ..
# get release tag name
tagName=`cat $PROPERTY_FILE | grep "scm.tag" | grep -i -v -E "scm.tagNameFormat" | cut -d'=' -f2`
# get release version
tagVersion=`cat $PROPERTY_FILE | grep "project.rel.com.github.vzakharchenko..keycloak-plugins"  | cut -d'=' -f2`
tagDevVersion=`cat $PROPERTY_FILE | grep "project.dev.com.github.vzakharchenko..keycloak-plugins"  | cut -d'=' -f2`

releaseNotes=`cat docs/release/${tagVersion}.txt`;

if [[ "x${tagVersion}" == "x" ]]; then
  echo "tagVersion is empty"
  exit 1;
fi

if [[ "x${tagName}" == "x" ]]; then
  echo "tagName is empty"
  exit 1;
fi
# get perform release
cd keycloak-plugins
mvn -Psign clean release:perform -Darguments=-Dgpg.passphrase=${password} -DskipTests
## build keycloak-radius
#cd ../keycloak
## update version of keycloak-radius-legacy
#mvn versions:set -DnewVersion=$tagVersion
## build keycloak-radius-legacy
#mvn clean install -Dkeycloak-plugin=$tagVersion -Dproduction=true -DskipTests
## update version of keycloak-radius-legacy
#mvn versions:set -DnewVersion=$tagDevVersion
cd ../keycloak-quarkus
# update version of keycloak-radius
mvn versions:set -DnewVersion=$tagVersion
mvn clean install -Dkeycloak-plugin=$tagVersion -Dproduction=true -DskipTests
# update version of keycloak-radius
mvn versions:set -DnewVersion=$tagDevVersion
cd ..
# create release
git pull
hub release create -a ./keycloak-quarkus/target/keycloak-radius.zip -m "Keycloak with radius server ${tagName}


**releaseNotes**:
<pre>
$releaseNotes
</pre>
- [Docker Installation](https://github.com/vzakharchenko/keycloak-radius-plugin/blob/master/docker/README.md)

requirements: **openjdk 11**

**Keycloak Quarkus installation steps**:
1. download and unzip keycloak-radius.zip <pre>unzip keycloak-radius.zip -d keycloak-radius</pre>
2. <pre>cd keycloak-radius</pre>
3. <pre>sh bin/kc.sh --debug 8190 start-dev --http-port=8090</pre>
4. open http://localhost:8090
5. default radius shared Secret: <pre>secret</pre>

**Keycloak WildFly (deprecated) installation steps**:
1. download and unzip keycloak-radius.zip <pre>unzip keycloak-radius-legacy.zip -d keycloak-radius</pre>
2. <pre>cd keycloak-radius</pre>
3. <pre>sh bin/standalone.sh  -c standalone.xml -b 0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --debug 8190 -Djboss.http.port=8090</pre>
4. open http://localhost:8090
5. default radius shared Secret: <pre>secret</pre>

" $tagName


