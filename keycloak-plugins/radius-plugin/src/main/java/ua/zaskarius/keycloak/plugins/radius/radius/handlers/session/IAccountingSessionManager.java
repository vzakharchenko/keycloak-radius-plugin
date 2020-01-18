package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

public interface IAccountingSessionManager {

    IAccountingSessionManager init();

    IAccountingSessionManager updateContext();

    IAccountingSessionManager manageSession();

    boolean isValidSession();
}
