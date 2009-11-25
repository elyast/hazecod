package robotframework.jdiameter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GlobalDefaultsTest {

    private GlobalDefaults testObj;

    @Before
    public void setup() {
	testObj = new GlobalDefaults();
    }

    @Test
    public void testGetDefaultEndToEndId() {
	assertEquals(19, testObj.getDefaultEndToEndId());
    }

    @Test
    public void testGetDefaultApplicationId() {
	assertEquals(4, testObj.getDefaultApplicationId());
    }

    @Test
    public void testGetDefaultHopByHopId() {
	assertEquals(37, testObj.getDefaultHopByHopId());
    }

    @Test
    public void testGetDefaultHost() {
	assertEquals("localhost", testObj.getDefaultHost());
    }

    @Test
    public void testGetDefaultPort() {
	assertEquals(3868, testObj.getDefaultPort());
    }

    @Test
    public void testGetDefaultTimeout() {
	assertEquals(300, testObj.getDefaultTimeout());
    }

}
