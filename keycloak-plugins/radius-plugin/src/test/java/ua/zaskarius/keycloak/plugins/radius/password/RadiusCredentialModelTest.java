package ua.zaskarius.keycloak.plugins.radius.password;

import ua.zaskarius.keycloak.plugins.radius.test.ModelBuilder;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

public class RadiusCredentialModelTest {
    @Test
    public void testCreateFromCredentialModel() {
        RadiusCredentialModel fromCredentialModel = RadiusCredentialModel
                .createFromCredentialModel(
                        ModelBuilder.createCredentialModel());
        assertNotNull(fromCredentialModel.getSecret());
        assertNotNull(fromCredentialModel.getCredential());
        assertEquals(fromCredentialModel.getSecret().getPassword(), "secret");
    }

    @Test
    public void testCreateFromValues() {
        RadiusCredentialModel fromCredentialModel = RadiusCredentialModel
                .createFromValues(
                        "111","1");
        assertNotNull(fromCredentialModel.getSecret());
        assertNotNull(fromCredentialModel.getCredential());
        assertEquals(fromCredentialModel.getSecret().getPassword(), "111");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateFromCredentialModelFail() {
        RadiusCredentialModel fromCredentialModel = RadiusCredentialModel
                .createFromCredentialModel(
                        ModelBuilder.createCredentialModel(null,"{sdfsd"));
        assertNotNull(fromCredentialModel.getSecret());
        assertNotNull(fromCredentialModel.getCredential());
        assertEquals(fromCredentialModel.getSecret().getPassword(), "secret");
    }

}
