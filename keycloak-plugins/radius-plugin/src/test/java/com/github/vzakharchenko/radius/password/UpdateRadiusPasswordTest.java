package com.github.vzakharchenko.radius.password;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import com.github.vzakharchenko.radius.test.ModelBuilder;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.InitiatedActionSupport;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class UpdateRadiusPasswordTest extends AbstractRadiusTest {
    private final UpdateRadiusPassword updateRadiusPassword =
            new UpdateRadiusPassword();

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
    public void methodTest() {
        updateRadiusPassword.close();
        updateRadiusPassword.init(null);
        updateRadiusPassword.postInit(keycloakSessionFactory);
        assertEquals(updateRadiusPassword.initiatedActionSupport(),
                InitiatedActionSupport.SUPPORTED);
        assertEquals(updateRadiusPassword.getId(), UpdateRadiusPassword.UPDATE_RADIUS_PASSWORD_ID);
        assertEquals(updateRadiusPassword.getDisplayText(), "Update Radius Password");
        assertTrue(updateRadiusPassword.isOneTimeAction());
        assertTrue(updateRadiusPassword.isOneTimeAction());
    }

    @Test
    public void testCreateDisplay() {
        assertEquals(updateRadiusPassword
                .createDisplay(session, null), updateRadiusPassword);
    }

    @Test
    public void testCreate() {
        assertEquals(updateRadiusPassword
                .create(session), updateRadiusPassword);
    }

    @Test
    public void testCreateDisplayConsole() {
        assertNull(updateRadiusPassword
                .createDisplay(session, "dfdsf"));
    }

    @Test
    public void testCreateDisplayConsoleUpdate() {
        assertEquals(updateRadiusPassword
                        .createDisplay(session, OAuth2Constants.DISPLAY_CONSOLE),
                ConsoleUpdateRadiusPassword.SINGLETON);
    }

    @Test
    public void testProcessActionUpdate() {
        updateRadiusPassword.processAction(context);
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
        updateRadiusPassword.processAction(context);
        verify(context).success();
        verify(userCredentialManager)
                .createCredential(any(), any(), any(CredentialModel.class));
    }

    @Test
    public void testProcessActionError() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE)).thenThrow(new ModelException());
        updateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionGlobalError() {
        when(userCredentialManager
                .getStoredCredentialsByType(realmModel, userModel,
                        RadiusCredentialModel.TYPE)).thenThrow(new RuntimeException());
        updateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionEmptyNewPassword() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        updateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testProcessActionNotEquals() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "111");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        updateRadiusPassword.processAction(context);
        verify(context).challenge(any());

    }

    @Test
    public void testProcessActionNotEquals2() {
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("password-new", "111");
        map.add("password-confirm", "1111");
        when(httpRequest.getDecodedFormParameters()).thenReturn(map);
        updateRadiusPassword.processAction(context);
        verify(context).challenge(any());
    }

    @Test
    public void testRequiredActionChallenge() {
        updateRadiusPassword.requiredActionChallenge(context);
        verify(context).challenge(any());
    }

    @Test
    public void evaluateTriggersTestExpiredPassword() {
        updateRadiusPassword.evaluateTriggers(context);
        verify(userModel).addRequiredAction(anyString());
    }

    @Test
    public void evaluateTriggersTestValid() {
        when(iRadiusCredentialProvider.getPassword(realmModel, userModel))
                .thenReturn(ModelBuilder.createCredentialModel(
                        System.currentTimeMillis()));
        updateRadiusPassword.evaluateTriggers(context);
        verify(userModel, never()).addRequiredAction(anyString());
    }

    @Test
    public void evaluateTriggersTestCreateDateIsNull() {
        when(iRadiusCredentialProvider.getPassword(realmModel, userModel))
                .thenReturn(ModelBuilder.createCredentialModel(
                        null));
        updateRadiusPassword.evaluateTriggers(context);
        verify(userModel).addRequiredAction(anyString());
    }

    @Test
    public void evaluateTriggersTestNotNeeded() {
        when(passwordPolicy.getDaysToExpirePassword()).thenReturn(-1);
        updateRadiusPassword.evaluateTriggers(context);
        verify(userModel, never()).addRequiredAction(anyString());
    }
}
