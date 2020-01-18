package ua.zaskarius.keycloak.plugins.radius.radius.holder;

public interface IRadiusUserInfoGetter {
    IRadiusUserInfo getRadiusUserInfo();

    IRadiusUserInfoBuilder getBuilder();
}
