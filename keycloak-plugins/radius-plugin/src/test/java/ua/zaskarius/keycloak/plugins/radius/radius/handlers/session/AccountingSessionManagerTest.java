package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.util.RadiusEndpoint;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;
import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.AccountingSessionManager.ACCT_SESSION_ID;
import static ua.zaskarius.keycloak.plugins.radius.radius.handlers.session.AccountingSessionManager.ACCT_STATUS_TYPE;

public class AccountingSessionManagerTest extends AbstractRadiusTest {
    private AccountingSessionManager accountingSessionManager;
    private AccountingRequest request;
    private RadiusEndpoint radiusEndpoint;

    @BeforeMethod
    public void beforeMethods() {
        request = new AccountingRequest(realDictionary, 0, new byte[16]);
        request.addAttribute("realm-radius", realmModel.getName());
        request.addAttribute("User-Name", userModel.getUsername());
        radiusEndpoint = new RadiusEndpoint(new InetSocketAddress(0), "test");
        accountingSessionManager = new AccountingSessionManager(request, session, radiusEndpoint);
    }

    @Test
    public void testInit() {
        IAccountingSessionManager accountingSessionManager = this.accountingSessionManager.init();
        assertNotNull(accountingSessionManager);
    }

    @Test
    public void testUpdateContext() {
        this.accountingSessionManager.init().updateContext();
    }

    @Test
    public void testManageSessionCreate() {
        request.addAttribute(ACCT_SESSION_ID, "new Session");
        request.addAttribute(ACCT_STATUS_TYPE, "Start");
        this.accountingSessionManager.init().updateContext().manageSession();
        verify(userSessionProvider).createUserSession(realmModel,
                userModel,
                "USER",
                "",
                "radius",
                false,
                null,
                null);
    }

    @Test
    public void testManageSessionReCreate() {
        request.addAttribute(ACCT_SESSION_ID, "new Session");
        request.addAttribute(ACCT_STATUS_TYPE, "Alive");
        this.accountingSessionManager.init().updateContext().manageSession();
        verify(userSessionProvider).createUserSession(realmModel,
                userModel,
                "USER",
                "",
                "radius",
                false,
                null,
                null);
    }

    @Test
    public void testManageSessionUpdateSession() {
        request.addAttribute(ACCT_SESSION_ID, RADIUS_SESSION_ID);
        request.addAttribute(ACCT_STATUS_TYPE, "Alive");
        this.accountingSessionManager.init().updateContext().manageSession();
    }

    @Test
    public void testManageSessionRemoveSession() {
        request.addAttribute(ACCT_SESSION_ID, RADIUS_SESSION_ID);
        request.addAttribute(ACCT_STATUS_TYPE, "Stop");
        this.accountingSessionManager.init().updateContext().manageSession();
        verify(userSessionProvider).removeUserSession(realmModel,
                userSessionModel);
    }

    @Test
    public void testManageSessionRemovenewSession() {
        request.addAttribute(ACCT_SESSION_ID, "new Session");
        request.addAttribute(ACCT_STATUS_TYPE, "Stop");
        this.accountingSessionManager.init().updateContext().manageSession();
        verify(userSessionProvider).removeUserSession(realmModel,
                userSessionModel);
    }


}
