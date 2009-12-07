package org.robotframework.jdiameter.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.mapper.GlobalDefaults;

public class GlobalDefaultsTest {

    private GlobalDefaults testObj;

    @Before
    public void setup() {
	testObj = new GlobalDefaults();
    }

    @Test
    public void testGetDefaultApplicationId() {
	assertEquals(19302, testObj.getDefaultApplicationId());
    }

}
