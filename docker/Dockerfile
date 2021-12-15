FROM jboss/keycloak:15.0.2
MAINTAINER Vasyl Zakharchenko <vaszakharchenko@gmail.com>
LABEL author="Vasyl Zakharchenko"
LABEL email="vaszakharchenko@gmail.com"
LABEL name="keycloak-radius-plugin"
COPY ./scripts /opt/radius/scripts
COPY ./cli /opt/radius/cli
COPY ./config /config
USER root
RUN chown -R  1000 /config
USER 1000
RUN /opt/radius/scripts/docker-radius.sh
RUN rm -rf /opt/jboss/keycloak/standalone/configuration/standalone_xml_history
EXPOSE 8080
EXPOSE 9990
ENTRYPOINT [ "/opt/radius/scripts/docker-radius-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]
