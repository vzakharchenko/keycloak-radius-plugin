package ua.zaskarius.keycloak.plugins.radius.radius.dictionary;

import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import static org.testng.Assert.*;

public class DefaultDictionaryProviderFactoryTest extends AbstractRadiusTest {
    private DefaultDictionaryProviderFactory dictionaryProviderFactory =
            new DefaultDictionaryProviderFactory();

    @Test
    public void testMethods() {
        dictionaryProviderFactory.close();
        dictionaryProviderFactory.init(null);
        dictionaryProviderFactory.postInit(null);
        assertNull(dictionaryProviderFactory.getRealmAttributes());
        assertNotNull(dictionaryProviderFactory.create(session));
        assertNotNull(dictionaryProviderFactory.getDictionaryParser());
        assertEquals(dictionaryProviderFactory.getId(), "Default-Dictionary");
        assertEquals(dictionaryProviderFactory.getResources().size(), 1);

    }
}
