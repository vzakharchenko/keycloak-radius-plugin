package com.github.vzakharchenko.radius.test;

import org.keycloak.common.util.ResteasyProvider;

public class TestResteasyProvider implements ResteasyProvider {
    @Override
    public <R> R getContextData(Class<R> type) {
        return null;
    }

    @Override
    public void pushDefaultContextObject(Class type, Object instance) {

    }

    @Override
    public void pushContext(Class type, Object instance) {

    }

    @Override
    public void clearContextData() {

    }
}
