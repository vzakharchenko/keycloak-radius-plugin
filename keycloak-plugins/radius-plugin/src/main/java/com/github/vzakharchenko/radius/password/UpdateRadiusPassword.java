package com.github.vzakharchenko.radius.password;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.ModelException;
import org.keycloak.models.UserModel;
import org.keycloak.services.validation.Validation;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.keycloak.events.Errors.PASSWORD_CONFIRM_ERROR;
import static org.keycloak.events.Errors.PASSWORD_MISSING;
import static org.keycloak.services.messages.Messages.MISSING_PASSWORD;
import static org.keycloak.services.messages.Messages.NOTMATCH_PASSWORD;


public class UpdateRadiusPassword implements RequiredActionProvider,
        RequiredActionFactory {
    public static final String UPDATE_RADIUS_PASSWORD_ID = "UPDATE_RADIUS_PASSWORD";
    public static final String USERNAME = "username";
    public static final String RADIUS_UPDATE_PASSWORD = UPDATE_RADIUS_PASSWORD_ID;
    public static final String UPDATE_PASSWORD_ERROR = "UPDATE_PASSWORD_ERROR";
    private static final Logger LOGGER = Logger.getLogger(UpdateRadiusPassword.class);

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    protected IRadiusCredentialProvider getProvider(RequiredActionContext context) {
        return
                (IRadiusCredentialProvider) context.getSession()
                        .getProvider(CredentialProvider.class,
                                RadiusCredentialProviderFactory.RADIUS_PROVIDER_ID);
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        int daysToExpirePassword = context.getRealm().getPasswordPolicy()
                .getDaysToExpirePassword();
        if (daysToExpirePassword != -1) {
            IRadiusCredentialProvider passwordProvider = getProvider(context);
            CredentialModel password = passwordProvider
                    .getPassword(context.getRealm(), context.getUser());
            if (password != null) {
                if (password.getCreatedDate() == null) {
                    context.getUser().addRequiredAction(getId());
                    LOGGER.debug("User is required to update Mikrotik password");
                } else {
                    long timeElapsed = Time.toMillis(Time.currentTime()) - password
                            .getCreatedDate();
                    long timeToExpire = TimeUnit.DAYS.toMillis(daysToExpirePassword);

                    if (timeElapsed > timeToExpire) {
                        context.getUser().addRequiredAction(getId());
                        LOGGER.debug("User is required to update password");
                    }
                }
            }
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form()
                .setAttribute(USERNAME, context.getAuthenticationSession()
                        .getAuthenticatedUser().getUsername())
                .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
        context.challenge(challenge);
    }

    protected EventBuilder createEvent(RequiredActionContext context,
                                       EventBuilder event) {
        return event.clone().event(EventType.UPDATE_PASSWORD_ERROR)
                .client(context.getAuthenticationSession().getClient())
                .user(context.getAuthenticationSession().getAuthenticatedUser())
                .detail(
                        UPDATE_PASSWORD_ERROR,
                        "Update Radius Server password error");
    }

    protected void commonResponse(RequiredActionContext context,
                                  EventBuilder errorEvent,
                                  String message,
                                  String eventMessage) {
        Response challenge = context.form()
                .setAttribute(USERNAME, context.getAuthenticationSession()
                        .getAuthenticatedUser().getUsername())
                .setError(message)
                .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
        context.challenge(challenge);
        errorEvent.error(eventMessage);
    }

    protected void success(RequiredActionContext context,
                           String passwordNew) {
        UserModel user = context.getUser();
        List<CredentialModel> credentials = user.credentialManager()
                .getStoredCredentialsByTypeStream(
                        RadiusCredentialModel.TYPE).collect(Collectors.toList());
        RadiusCredentialModel credentialModel = RadiusCredentialModel
                .createFromValues(passwordNew, user.getId());
        if (credentials.isEmpty()) {
            user.credentialManager()
                    .createStoredCredential(
                            credentialModel);
        } else {
            user.credentialManager()
                    .updateStoredCredential(
                            credentialModel);
        }
        context.success();
    }

    protected void exceptionHandler(EventBuilder errorEvent,
                                    RequiredActionContext context,
                                    Exception e) {
        Object[] parameters = (e instanceof ModelException me) ?
                me.getParameters() :
                new Object[0];
        errorEvent.detail(Details.REASON, e.getMessage()).error(Errors.PASSWORD_REJECTED);
        Response challenge = context.form()
                .setAttribute(USERNAME, context
                        .getAuthenticationSession().getAuthenticatedUser().getUsername())
                .setError(e.getMessage(), parameters)
                .createResponse(UserModel.RequiredAction.UPDATE_PASSWORD);
        context.challenge(challenge);
    }


    @Override
    public void processAction(RequiredActionContext context) {
        EventBuilder event = context.getEvent();
        MultivaluedMap<String, String> formData = context.getHttpRequest()
                .getDecodedFormParameters();
        event.event(EventType.UPDATE_PASSWORD);
        event.detail(
                RADIUS_UPDATE_PASSWORD,
                "Update Radius Server password");
        String passwordNew = formData.getFirst("password-new");
        String passwordConfirm = formData.getFirst("password-confirm");
        EventBuilder errorEvent = createEvent(context, event);
        if (Validation.isBlank(passwordNew)) {
            commonResponse(context, errorEvent, MISSING_PASSWORD, PASSWORD_MISSING);
        } else if (!passwordNew.equals(passwordConfirm)) {
            commonResponse(context, errorEvent, NOTMATCH_PASSWORD, PASSWORD_CONFIRM_ERROR);
        } else {
            try {
                success(context, passwordNew);
            } catch (Exception ape) {
                exceptionHandler(errorEvent, context, ape);
            }
        }
    }

    @Override
    public void close() {

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getDisplayText() {
        return "Update Radius Password";
    }


    @Override
    public String getId() {
        return UPDATE_RADIUS_PASSWORD_ID;
    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }
}
