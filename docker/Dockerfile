ARG KEYCLOAK_VERSION=21.0.0

FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION}
LABEL author="Vasyl Zakharchenko"
LABEL email="vaszakharchenko@gmail.com"
LABEL name="keycloak-radius-plugin"

ARG PLUGIN_VERSION=1.4.11

COPY ./scripts /opt/radius/scripts
ENV RADIUS_CONFIG_PATH=/opt/keycloak/config

# Adding plugins
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/radius-plugin/${PLUGIN_VERSION}/radius-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/radius-plugin.jar
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/proxy-radius-plugin/${PLUGIN_VERSION}/proxy-radius-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/proxy-radius-plugin.jar
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/rad-sec-plugin/${PLUGIN_VERSION}/rad-sec-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/rad-sec-plugin.jar
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/mikrotik-radius-plugin/${PLUGIN_VERSION}/mikrotik-radius-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/mikrotik-radius-plugin.jar
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/cisco-radius-plugin/${PLUGIN_VERSION}/cisco-radius-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/cisco-radius-plugin.jar
ADD https://repository.sonatype.org/service/local/repositories/central-proxy/content/com/github/vzakharchenko/chillispot-radius-plugin/${PLUGIN_VERSION}/chillispot-radius-plugin-${PLUGIN_VERSION}.jar /opt/keycloak/providers/chillispot-radius-plugin.jar

USER 1000

EXPOSE 8080
EXPOSE 9990
EXPOSE 2083
EXPOSE 1812/udp
EXPOSE 1813/udp
ENTRYPOINT [ "/opt/radius/scripts/docker-radius-entrypoint.sh" ]

#CMD ["-b", "0.0.0.0"]
