package com.github.vzakharchenko.radius.dm.models;

public final class DisconnectMessageModelBuilder {
    private final DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();

    private DisconnectMessageModelBuilder() {
    }


    public static DisconnectMessageModelBuilder create() {
        return new DisconnectMessageModelBuilder();
    }

    public DisconnectMessageModelBuilder radiusSessionId(String sessionId) {
        disconnectMessageModel.setRadiusSessionId(sessionId);
        return this;
    }

    public DisconnectMessageModelBuilder clientId(String clientId) {
        disconnectMessageModel.setClientId(clientId);
        return this;
    }

    public DisconnectMessageModelBuilder userId(String userId) {
        disconnectMessageModel.setUserId(userId);
        return this;
    }

    public DisconnectMessageModelBuilder realmId(String realmId) {
        disconnectMessageModel.setRealmId(realmId);
        return this;
    }

    public DisconnectMessageModelBuilder address(String address) {
        disconnectMessageModel.setAddress(address);
        return this;
    }

    public DisconnectMessageModelBuilder secret(String secret) {
        disconnectMessageModel.setSecret(secret);
        return this;
    }

    public DisconnectMessageModelBuilder userName(String userName) {
        disconnectMessageModel.setUserName(userName);
        return this;
    }

    public DisconnectMessageModelBuilder nasPort(String nasPort) {
        disconnectMessageModel.setNasPort(nasPort);
        return this;
    }

    public DisconnectMessageModelBuilder nasPortType(String nasPortType) {
        disconnectMessageModel.setNasPortType(nasPortType);
        return this;
    }

    public DisconnectMessageModelBuilder nasIp(String nasIp) {
        disconnectMessageModel.setNasIp(nasIp);
        return this;
    }

    public DisconnectMessageModelBuilder framedIp(String framedIp) {
        disconnectMessageModel.setFramedIp(framedIp);
        return this;
    }

    public DisconnectMessageModelBuilder callingStationId(String callingStationId) {
        disconnectMessageModel.setCallingStationId(callingStationId);
        return this;
    }

    public DisconnectMessageModelBuilder keycloakSessionId(String keycloakSessionId) {
        disconnectMessageModel.setKeycloakSessionId(keycloakSessionId);
        return this;
    }

    public DisconnectMessageModel build() {
        return disconnectMessageModel;
    }
}
