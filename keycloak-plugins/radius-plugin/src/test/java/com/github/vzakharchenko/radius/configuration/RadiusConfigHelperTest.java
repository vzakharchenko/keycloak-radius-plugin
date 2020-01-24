package com.github.vzakharchenko.radius.configuration;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class RadiusConfigHelperTest extends AbstractRadiusTest {
    @Test
    public void testConfig(){
        assertNotNull(RadiusConfigHelper.getConfig());
    }
}
