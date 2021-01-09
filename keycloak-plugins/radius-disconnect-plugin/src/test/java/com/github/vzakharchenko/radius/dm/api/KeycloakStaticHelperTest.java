package com.github.vzakharchenko.radius.dm.api;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

public class KeycloakStaticHelperTest extends AbstractRadiusTest {
    @Test(expectedExceptions = Throwable.class)
    public void testMethod() {
        KeycloakStaticHelper keycloakStaticHelper =
                new KeycloakStaticHelperImpl();
        keycloakStaticHelper.getAccessToken(session);
    }
}
