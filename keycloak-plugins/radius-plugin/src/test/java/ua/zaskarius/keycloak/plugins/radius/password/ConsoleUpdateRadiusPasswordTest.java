package ua.zaskarius.keycloak.plugins.radius.password;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.ModelException;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConsoleUpdateRadiusPasswordTest extends AbstractRadiusTest {
    private ConsoleUpdateRadiusPassword consoleUpdateRadiusPassword =
            ConsoleUpdateRadiusPassword.SINGLETON;

    @Mock
    private RequiredActionContext context;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private AuthenticationSessionModel authenticationSessionModel;

    @Mock
    private LoginFormsProvider loginFormsProvider;

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private IRadiusCredentialProvider iRadiusCredentialProvider;

    @BeforeMethod
    public void beforeMethod() {
        reset(context);
        reset(httpRequest);
        reset(authenticationSessionModel);
        reset(loginFormsProvider);
        reset(passwordPolicy);
        when(context.getSession()).thenReturn(session);
        when(context
                .getEvent())
                .thenReturn(eventBuilder);
        when(context.getHttpRequest()).thenReturn(httpRequest);
        when(context.getUser()).thenReturn(userModel);
        when(context.getRealm()).thenReturn(realmModel);
        when(context.getActionUrl(anyBoolean())).thenReturn(URI.create("test"));
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "1111");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        when(authenticationSessionModel.getAuthenticatedUser()).thenReturn(userModel);
        when(authenticationSessionModel.getClient()).thenReturn(clientModel);
        when(authenticationSessionModel.getRealm()).thenReturn(realmModel);
        when(context.getAuthenticationSession()).thenReturn(authenticationSessionModel);
        when(context.form()).thenReturn(loginFormsProvider);
        when(loginFormsProvider.setAttribute(anyString(), any())).thenReturn(loginFormsProvider);
        when(loginFormsProvider.setAttribute(anyString(), any())).thenReturn(loginFormsProvider);
        when(loginFormsProvider.setError(any(), any())).thenReturn(loginFormsProvider);
        when(loginFormsProvider.setError(any())).thenReturn(loginFormsProvider);
        when(loginFormsProvider
                .createResponse(any()))
                .thenReturn(Response.accepted().build());
        when(realmModel.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(passwordPolicy.getDaysToExpirePassword()).thenReturn(1);

        when(session.getProvider(eq(CredentialProvider.class), anyString()))
                .thenReturn(iRadiusCredentialProvider);
        when(iRadiusCredentialProvider.getPassword(realmModel, userModel))
                .thenReturn(ModelBuilder.createCredentialModel());
    }

    @Override
    protected List<? extends Object> resetMock() {
        return null;
    }


    @Test
    public void testProcessActionUpdate() {
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).success();
        verify(userCredentialManager)
                .updateCredential(any(), any(), any(CredentialModel.class));
    }


    @Test
    public void testProcessActionCreate() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE))
                .thenReturn(new ArrayList<>());
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).success();
        verify(userCredentialManager)
                .createCredential(any(), any(), any(CredentialModel.class));
    }

    @Test
    public void testProcessActionError() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE)).thenThrow(new ModelException());
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionGlobalError() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE)).thenThrow(new RuntimeException());
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionEmptyNewPassword() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionNotEquals() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "111");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).challenge(any());

    }

    @Test
    public void testProcessActionNotEquals2() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "111");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        consoleUpdateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testRequiredActionChallenge() {
        consoleUpdateRadiusPassword.requiredActionChallenge(context);
        verify(context).challenge(any());
    }


}
