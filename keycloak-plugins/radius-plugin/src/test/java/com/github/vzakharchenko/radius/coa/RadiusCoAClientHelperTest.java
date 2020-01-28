package com.github.vzakharchenko.radius.coa;

import com.github.vzakharchenko.radius.test.AbstractRadiusTest;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.testng.Assert.assertEquals;

public class RadiusCoAClientHelperTest extends AbstractRadiusTest {
    @Test
    public void testCoaClient() {
        RadiusCoAClientHelper.setRadiusCoAClient(new RadiusCoAClient());
        RadiusCoAClientHelper.requestCoA(realDictionary, radiusClient -> {

        }, null);
    }

    @Test
    public void testCoaClientException() {
        doThrow(new IllegalStateException("test")).when(radiusCoAClient).requestCoA(any(), any());
        RadiusCoAClientHelper.requestCoA(realDictionary, radiusClient -> {
        }, null);
    }

    @Test
    public void testCoaClientException2() {
        IllegalStateException test = new IllegalStateException("test");
        doThrow(test).when(radiusCoAClient).requestCoA(any(), any());
        RadiusCoAClientHelper.requestCoA(realDictionary, radiusClient -> {
        }, ex -> assertEquals(ex, test));
    }
}
