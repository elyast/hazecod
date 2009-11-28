package org.robotframework.jdiameter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.UserParameterParser;

public class UserParameterParserTest {

    private UserParameterParser testObj;

    @Before
    public void setup() {
	testObj = new UserParameterParser();
    }

    @Test
    public void testCreateEntry_OK() throws Exception {
	Map.Entry<String, String> v = testObj.createEntry(" key =  value ");
	assertEquals("key", v.getKey());
	assertEquals("value", v.getValue());

	v = testObj.createEntry("key=value");
	assertEquals("key", v.getKey());
	assertEquals("value", v.getValue());

    }

    @Test(expected = RuntimeException.class)
    public void tesCreateEntry_TooManyEqualSign() throws Exception {
	testObj.createEntry("key=val=ue");
    }

    @Test(expected = RuntimeException.class)
    public void tesCreateEntry_NoEqualSign() throws Exception {
	testObj.createEntry("keysdfsdf");
    }

    @Test
    public void testParse_2Params() throws Exception {
	List<Object> parameters = new ArrayList<Object>();
	parameters.add("ke=v");
	parameters.add("ve = 1");

	List<Map.Entry<String, String>> result = testObj.parse(parameters);
	assertEquals("ke", result.get(0).getKey());
	assertEquals("v", result.get(0).getValue());

	assertEquals("ve", result.get(1).getKey());
	assertEquals("1", result.get(1).getValue());

    }

    @Test
    public void testParse_NoParams() throws Exception {
	List<Object> parameters = new ArrayList<Object>();

	List<Map.Entry<String, String>> result = testObj.parse(parameters);
	assertEquals(0, result.size());
    }

    @Test
    public void testParse_Null() throws Exception {
	List<Map.Entry<String, String>> result = testObj.parse(null);
	assertEquals(0, result.size());
    }
}
