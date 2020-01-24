package com.github.vzakharchenko.radius.password;

import org.keycloak.authentication.ConsoleDisplayMode;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.services.messages.Messages;


public class ConsoleUpdateRadiusPassword extends UpdateRadiusPassword {

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
    protected void blankResponse(RequiredActionContext context, EventBuilder errorEvent) {
        context.challenge(challenge(context).message(Messages.MISSING_PASSWORD));
        errorEvent.error(Errors.PASSWORD_MISSING);
    }

    @Override
    protected void notEqualsResponse(RequiredActionContext context,
                                     EventBuilder errorEvent) {
        context.challenge(challenge(context).message(Messages.NOTMATCH_PASSWORD));
        errorEvent.error(Errors.PASSWORD_CONFIRM_ERROR);
    }

    @Override
    protected void exceptionHandler(EventBuilder errorEvent,
                                    RequiredActionContext context, Exception e) {
        errorEvent.detail(Details.REASON, e.getMessage()).error(Errors.PASSWORD_REJECTED);
        context.challenge(challenge(context).text(e.getMessage()));
    }
}
