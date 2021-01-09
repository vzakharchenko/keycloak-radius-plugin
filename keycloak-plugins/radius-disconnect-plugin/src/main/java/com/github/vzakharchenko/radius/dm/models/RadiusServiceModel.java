package com.github.vzakharchenko.radius.dm.models;

import java.util.List;

public class RadiusServiceModel {

    private String clientId;

    private String sessionId;

    private String address;

    private String userName;

    private String nasPort;

    private String nasPortType;

    private String nasIp;

    private String framedIp;

    private String callingStationId;

    private String calledStationId;

    private List<String> roles;

    private List<String> groups;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNasPort() {
        return nasPort;
    }

    public void setNasPort(String nasPort) {
        this.nasPort = nasPort;
    }

    public String getNasPortType() {
        return nasPortType;
    }

    public void setNasPortType(String nasPortType) {
        this.nasPortType = nasPortType;
    }

    public String getNasIp() {
        return nasIp;
    }

    public void setNasIp(String nasIp) {
        this.nasIp = nasIp;
    }

    public String getFramedIp() {
        return framedIp;
    }

    public void setFramedIp(String framedIp) {
        this.framedIp = framedIp;
    }

    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }
}
