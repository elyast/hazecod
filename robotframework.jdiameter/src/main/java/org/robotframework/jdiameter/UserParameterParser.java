package org.robotframework.jdiameter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Parser user parameters into list of key, value
 * 
 */
public class UserParameterParser {

    /**
     * Do the parsing
     * 
     * @param parameters
     *            List of string with format (key = value)
     * @return List of map.entry
     */
    public List<Entry<String, String>> parse(List<Object> parameters) {
	if (parameters == null) {
	    return new ArrayList<Entry<String, String>>(0);
	}

	List<Entry<String, String>> result = new ArrayList<Entry<String, String>>(
		parameters.size());
	for (Object object : parameters) {
	    String avp = String.valueOf(object);
	    Entry<String, String> entry = createEntry(avp);
	    result.add(entry);
	}
	return result;
    }

    /**
     * Parses string with following regexp: key=value
     * 
     * @param avp
     * @return
     */
    Entry<String, String> createEntry(String avp) {
	String[] splitted = avp.split("=");
	if (splitted.length != 2) {
	    throw new RuntimeException("Invalid syntax: key=value expected");
	}
	String key = splitted[0].trim();
	String value = splitted[1].trim();
	return new AbstractMap.SimpleEntry<String, String>(key, value);
    }

}
