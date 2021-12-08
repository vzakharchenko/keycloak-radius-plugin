package com.github.vzakharchenko.radius.radius.holder;

import com.github.vzakharchenko.radius.radius.handlers.session.PasswordData;
import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.util.Collections;

import static org.testng.Assert.*;

public class RadiusUserInfoTest extends AbstractRadiusTest {

    private final RadiusUserInfoBuilder radiusUserInfoBuilder = (RadiusUserInfoBuilder)
            RadiusUserInfoBuilder.create();

    @Test
    public void testMethods() {
        IRadiusUserInfoGetter radiusUserInfoGetter = radiusUserInfoBuilder.addPasswords(
                        Collections.singletonList(PasswordData.create("test")))
                .clientConnection(clientConnection).clientModel(clientModel)
                .address(new InetSocketAddress(0))
                .userModel(userModel).realmModel(realmModel).activePassword("test").forceReject()
                .protocol(authProtocol).radiusSecret("testSecret").getRadiusUserInfoGetter();
        IRadiusUserInfo radiusUserInfo = radiusUserInfoGetter.getRadiusUserInfo();
        assertEquals(radiusUserInfoGetter.getBuilder(), radiusUserInfoGetter);
        assertEquals(radiusUserInfo.getRealmModel(), realmModel);
        assertEquals(radiusUserInfo.getUserModel(), userModel);
        assertEquals(radiusUserInfo.getClientConnection(), clientConnection);
        assertEquals(radiusUserInfo.getPasswords().size(), 1);
        assertEquals(radiusUserInfo.getClientModel(), clientModel);
        assertEquals(radiusUserInfo.getActivePassword(), "test");
        assertEquals(radiusUserInfo.getProtocol(), authProtocol);
        assertEquals(radiusUserInfo.getRadiusSecret(), "testSecret");
        assertNotNull(radiusUserInfo.getAddress());
        assertTrue(radiusUserInfo.isForceReject());


    }
}
