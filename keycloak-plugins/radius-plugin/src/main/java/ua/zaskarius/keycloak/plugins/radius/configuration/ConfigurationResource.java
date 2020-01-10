package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.jboss.resteasy.annotations.cache.NoCache;
import ua.zaskarius.keycloak.plugins.radius.models.ConfigurationRepresentation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface ConfigurationResource {

    @POST
    @Path("admin/config/save")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ConfigurationRepresentation saveConfig(ConfigurationRepresentation configuration);

    @GET
    @Path("admin/config/get")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    ConfigurationRepresentation getConfig();
}
