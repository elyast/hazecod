package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.UserParameterTransformer;

public class UserParameterTransformerTest {

    private UserParameterTransformer testObj;

    @Before
    public void setup() {
	testObj = new UserParameterTransformer();
    }

    @Test
    public void testTransform_WithMapping() {
	// Properties mapping = new Properties();
	// mapping.put("Al", "A.B.C");
	// mapping.put("Bl", Arrays.asList(new String[] {"A.G", "B.C"}));
	Map<String, List<String>> mapping = new HashMap<String, List<String>>();

	List<String> values1 = new ArrayList<String>();
	values1.add("A.B.C");
	mapping.put("Al", values1);

	List<String> values2 = new ArrayList<String>();
	values2.add("A.G");
	values2.add("B.C");
	mapping.put("Bl", values2);

	List<Entry<String, String>> userParameters = new ArrayList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("Al",
		"11"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("A.G.D",
		"12"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("Bl",
		"13"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("A",
		"14"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("Al",
		"15"));

	List<Entry<String, String>> qualified = testObj.transform(mapping,
		userParameters);

	// for(Entry<String,String> entry : qualified){
	// entry.getKey()
	// }
	assertEquals("A.B.C", qualified.get(0).getKey());
	assertEquals("11", qualified.get(0).getValue());

	assertEquals("A.G.D", qualified.get(1).getKey());
	assertEquals("12", qualified.get(1).getValue());

	assertEquals("A.G", qualified.get(2).getKey());
	assertEquals("13", qualified.get(2).getValue());

	assertEquals("B.C", qualified.get(3).getKey());
	assertEquals("13", qualified.get(3).getValue());

	assertEquals("A", qualified.get(4).getKey());
	assertEquals("14", qualified.get(4).getValue());

	assertEquals("A.B.C", qualified.get(5).getKey());
	assertEquals("15", qualified.get(5).getValue());
    }

    @Test
    public void testTransform_NullParams() throws Exception {
	List<Entry<String, String>> qualified = testObj.transform(null, null);
	assertEquals(0, qualified.size());
    }

}
