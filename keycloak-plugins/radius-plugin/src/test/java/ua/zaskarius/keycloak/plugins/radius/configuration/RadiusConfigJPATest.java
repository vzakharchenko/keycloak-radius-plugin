package ua.zaskarius.keycloak.plugins.radius.configuration;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.zaskarius.keycloak.plugins.radius.models.RadiusConfigModel;
import ua.zaskarius.keycloak.plugins.radius.test.AbstractRadiusTest;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class RadiusConfigJPATest extends AbstractRadiusTest {
    private RadiusConfigJPA configJPA;


    @BeforeMethod
    public void method() {
        configJPA = new RadiusConfigJPA(session);
    }

    @Test
    public void testGetConfig() {
        RadiusConfigModel configModel = new RadiusConfigModel();
        configModel.setmDate(new Date(1L));
        configModel.setmUserId("111");
        configModel.setAccountPort(111);
        configModel.setAuthPort(11);
        configModel.setRadiusShared("234");
        configModel.setStart(true);
        when(query.getResultList()).thenReturn(Collections.singletonList(configModel));
        RadiusConfigModel config = configJPA.getConfig();
        assertNotNull(config);
        assertEquals(config.getmUserId(), "111");
    }

    @Test
    public void testGetConfigNull() {
        RadiusConfigModel configModel = new RadiusConfigModel();
        configModel.setmDate(new Date(1L));
        configModel.setmUserId("111");
        configModel.setAccountPort(111);
        configModel.setAuthPort(11);
        configModel.setRadiusShared("234");
        configModel.setStart(true);
        when(query.getResultList()).thenReturn(null);
        RadiusConfigModel config = configJPA.getConfig();
        assertNull(config);
    }

    @Test
    public void testGetConfigEmpty() {
        RadiusConfigModel configModel = new RadiusConfigModel();
        configModel.setmDate(new Date(1L));
        configModel.setmUserId("111");
        configModel.setAccountPort(111);
        configModel.setAuthPort(11);
        configModel.setRadiusShared("234");
        configModel.setStart(true);
        when(query.getResultList()).thenReturn(Collections.emptyList());
        RadiusConfigModel config = configJPA.getConfig();
        assertNull(config);
    }

    @Test
    public void testUpdateConfig() {
        RadiusConfigModel configModel = new RadiusConfigModel();
        configModel.setmDate(new Date(1L));
        configModel.setmUserId("111");
        configModel.setId("1");
        configModel.setAccountPort(111);
        configModel.setAuthPort(11);
        configModel.setRadiusShared("234");
        configModel.setStart(true);
        when(query.getResultList()).thenReturn(Collections.singletonList(configModel));
        RadiusConfigModel configModel2 = new RadiusConfigModel();
        configModel2.setmDate(new Date(1L));
        configModel2.setmUserId("111");
        configModel2.setId("0");
        configModel2.setAccountPort(111);
        configModel2.setAuthPort(11);
        configModel2.setRadiusShared("234");
        configModel2.setStart(true);

        configJPA.saveConfig(configModel2, userModel);
        verify(entityManager).persist(configModel2);
        assertEquals(configModel2.getId(), "1");
    }

    @Test
    public void testUpdateConfig2() {
        RadiusConfigModel configModel = new RadiusConfigModel();
        configModel.setmDate(new Date(1L));
        configModel.setmUserId("111");
        configModel.setId("1");
        configModel.setAccountPort(111);
        configModel.setAuthPort(11);
        configModel.setRadiusShared("234");
        configModel.setStart(true);
        when(query.getResultList()).thenReturn(null);
        RadiusConfigModel configModel2 = new RadiusConfigModel();
        configModel2.setmDate(new Date(1L));
        configModel2.setmUserId("111");
        configModel2.setId("0");
        configModel2.setAccountPort(111);
        configModel2.setAuthPort(11);
        configModel2.setRadiusShared("234");
        configModel2.setStart(true);

        configJPA.saveConfig(configModel2, userModel);
        verify(entityManager).persist(configModel2);
        assertNotEquals(configModel2.getId(), "0");
        assertNotEquals(configModel2.getId(), "1");
        assertNotNull(configModel2.getId());
    }


}
