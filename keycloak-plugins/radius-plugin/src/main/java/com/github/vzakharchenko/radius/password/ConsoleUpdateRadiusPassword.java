package com.github.vzakharchenko.radius.password;

import org.keycloak.authentication.ConsoleDisplayMode;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;


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
    protected void commonResponse(RequiredActionContext context,
                                  EventBuilder errorEvent,
                                  String message,
                                  String eventMessage) {
        context.challenge(challenge(context).message(message));
        errorEvent.error(eventMessage);
    }

    @Override
    protected void exceptionHandler(EventBuilder errorEvent,
                                    RequiredActionContext context, Exception e) {
        errorEvent.detail(Details.REASON, e.getMessage()).error(Errors.PASSWORD_REJECTED);
        context.challenge(challenge(context).text(e.getMessage()));
    }
}
