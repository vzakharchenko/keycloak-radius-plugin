package ua.zaskarius.keycloak.plugins.radius.radius.holder;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class RadiusUserInfoTest extends AbstractRadiusTest {

    private RadiusUserInfoBuilder radiusUserInfoBuilder = (RadiusUserInfoBuilder)
            RadiusUserInfoBuilder.create();

    @Test
    public void testMethods() {
        IRadiusUserInfoGetter radiusUserInfoGetter = radiusUserInfoBuilder.addPasswords(Arrays.asList("test"))
                .clientConnection(clientConnection).clientModel(clientModel)
                .userModel(userModel).realmModel(realmModel).activePassword("test")
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


    }
}
