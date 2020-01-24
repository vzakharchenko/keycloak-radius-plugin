package com.github.vzakharchenko.radius.radius.handlers.session;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class KeycloakSessionUtilsTest extends AbstractRadiusTest {
    @Test
    public void getRadiusInfo(){
        assertNotNull(KeycloakSessionUtils.getRadiusUserInfo(session));
    }
}
