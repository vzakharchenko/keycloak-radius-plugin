package com.github.vzakharchenko.radius.coa;

import org.tinyradius.client.RadiusClient;

public interface ICoaRequestHandler {
    void call(RadiusClient radiusClient);
}
