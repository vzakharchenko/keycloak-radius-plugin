package com.github.vzakharchenko.radius.dm.api;

import com.github.vzakharchenko.radius.dm.models.RadiusInfoModel;
import com.github.vzakharchenko.radius.dm.models.RadiusServiceModel;
import org.jboss.resteasy.reactive.NoCache;
import org.keycloak.models.ClientModel;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.utils.MediaType;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

public interface RadiusService extends RealmResourceProvider {
    /**
     * Create "Radius Session Role" during creating radius client
     * @param clientModel
     */
    void init(ClientModel clientModel);

    /**
     * Get Connection and User Info
     * Only Users(or service accounts) with Role "Radius Session Role" has access
     * @param  ip vpn remote Ip
     * @param calledStationId uniq Id. Radsecproxy:
     *  rewrite radsec {
     *     addAttribute 30:'%randomId%
     *  }
     * @return Connection and User Info
     */
    @GET
    @Path("v1/radius/users")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    RadiusServiceModel getActiveUser(@QueryParam("ip")  String ip,
                                     @QueryParam("calledStationId")  String calledStationId);

    /**
     * Kill session if VPN  connection is close
     * Only Users(or service accounts) with Role "Radius Session Role" has access
     * @param ip vpn remote Ip
     * @param calledStationId uniq Id. Radsecproxy:
     *  rewrite radsec {
     *     addAttribute 30:'123456
     *  }
     * @return Connection and User Info
     */
    @GET
    @Path("v1/radius/logout")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    RadiusServiceModel logout(@QueryParam("ip")  String ip,
                              @QueryParam("calledStationId")  String calledStationId);

    /**
     * get Radius Server information
     * Only Users(or service accounts) with Role "Radius Session Role" has access
     * API provide access to shared secret
     * @param calledStationId uniq Id. Radsecproxy:
     *  rewrite radsec {
     *     addAttribute 30:'123456
     *  }
     * @return json with shared secret, radsec enabled, udp enabled, useCoA
     */
    @GET
    @Path("v1/radius/info")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    RadiusInfoModel getRadiusInfo(
            @QueryParam("calledStationId")  String calledStationId);


}
