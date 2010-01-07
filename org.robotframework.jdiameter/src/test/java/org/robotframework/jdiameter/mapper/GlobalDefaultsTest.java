package org.robotframework.jdiameter.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Eliot
 *
 */
public class GlobalDefaultsTest {

    static final int SOME_APP_ID = 19302;
    private GlobalDefaults testObj;

    @Before
    public void setup() {
	testObj = new GlobalDefaults();
    }

    @Test
    public void testGetDefaultApplicationId() {
	assertEquals(SOME_APP_ID, testObj.getDefaultApplicationId());
    }

}
