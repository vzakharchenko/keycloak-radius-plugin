package ua.zaskarius.keycloak.plugins.radius.radius.handlers.session;

import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class KeycloakSessionUtilsTest extends AbstractRadiusTest {
    @Test
    public void getRadiusInfo(){
        assertNotNull(KeycloakSessionUtils.getRadiusUserInfo(session));
    }
}
