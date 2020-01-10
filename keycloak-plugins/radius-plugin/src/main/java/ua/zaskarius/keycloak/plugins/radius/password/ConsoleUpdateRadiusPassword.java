
package ua.zaskarius.keycloak.plugins.radius.password;

import org.keycloak.authentication.ConsoleDisplayMode;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.requiredactions.UpdatePassword;
import org.keycloak.credential.CredentialModel;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;


public class ConsoleUpdateRadiusPassword extends UpdatePassword {

    public static final ConsoleUpdateRadiusPassword SINGLETON =
            new ConsoleUpdateRadiusPassword();

    public static final String PASSWORD_NEW = "password-new";
    public static final String PASSWORD_CONFIRM = "password-confirm";

    protected ConsoleDisplayMode challenge(RequiredActionContext context) {
        return ConsoleDisplayMode.challenge(context)
                .header()
                .param(PASSWORD_NEW)
                .label("console-new-password")
                .mask(true)
                .param(PASSWORD_CONFIRM)
                .label("console-confirm-password")
                .mask(true)
                .challenge();
    }


    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        context.challenge(
                challenge(context).message("console-radius-update-password"));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        EventBuilder event = context.getEvent();
        MultivaluedMap<String, String> formData = context
                .getHttpRequest().getDecodedFormParameters();
        event.event(EventType.UPDATE_PASSWORD);
        String passwordNew = formData.getFirst(PASSWORD_NEW);
        String passwordConfirm = formData.getFirst(PASSWORD_CONFIRM);

        EventBuilder errorEvent = event.clone().event(EventType.UPDATE_PASSWORD_ERROR)
                .client(context.getAuthenticationSession().getClient())
                .user(context.getAuthenticationSession().getAuthenticatedUser());

        if (Validation.isBlank(passwordNew)) {
            context.challenge(challenge(context).message(Messages.MISSING_PASSWORD));
            errorEvent.error(Errors.PASSWORD_MISSING);
            return;
        } else if (!passwordNew.equals(passwordConfirm)) {
            context.challenge(challenge(context).message(Messages.NOTMATCH_PASSWORD));
            errorEvent.error(Errors.PASSWORD_CONFIRM_ERROR);
            return;
        }

        try {
            RealmModel realm = context.getRealm();
            UserModel user = context.getUser();
            List<CredentialModel> credentials = context
                    .getSession()
                    .userCredentialManager()
                    .getStoredCredentialsByType(realm,
                            user,
                            RadiusCredentialModel.TYPE);
            RadiusCredentialModel credentialModel = RadiusCredentialModel
                    .createFromValues(passwordNew, user.getId());
            if (credentials.isEmpty()) {
                context
                        .getSession()
                        .userCredentialManager()
                        .createCredential(realm, user,
                                credentialModel);
            } else {
                context.getSession().userCredentialManager()
                        .updateCredential(realm, user,
                                credentialModel);
            }
            context.success();
        } catch (ModelException me) {
            errorEvent.detail(Details.REASON, me.getMessage()).error(Errors.PASSWORD_REJECTED);
            context.challenge(challenge(context).text(me.getMessage()));
            return;
        } catch (Exception ape) {
            errorEvent.detail(Details.REASON, ape.getMessage()).error(Errors.PASSWORD_REJECTED);
            context.challenge(challenge(context).text(ape.getMessage()));
            return;
        }
    }
}
