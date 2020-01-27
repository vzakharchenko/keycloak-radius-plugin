package com.github.vzakharchenko.radius.coa;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

public class RadiusCoAClientHelperTest extends AbstractRadiusTest {
    @Test
    public void testCoaClient() {
        RadiusCoAClientHelper.setRadiusCoAClient(new RadiusCoAClient());
        RadiusCoAClientHelper.requestCoA(realDictionary, radiusClient -> {

        });
    }
}
