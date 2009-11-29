package org.robotframework.jdiameter;

import static org.junit.Assert.*;

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
	assertEquals(4, testObj.getDefaultApplicationId());
    }

}
