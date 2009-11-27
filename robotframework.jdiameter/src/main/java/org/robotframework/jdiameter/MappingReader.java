package org.robotframework.jdiameter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingReader extends PropertiesReader {

    private Map<String, List<String>> mappings = new HashMap<String, List<String>>();

    public MappingReader() {
    }

    private void transformProperties() {
	for (String key : props.stringPropertyNames()) {
	    List<String> values = new ArrayList<String>();
	    for (String value : props.getProperty(key).split(",")) {
		if (!value.isEmpty()) {
		    values.add(value.trim());
		}
	    }
	    mappings.put(key, values);
	}
    }

    public List<String> getMapping(String key) {
	List<String> result = mappings.get(key);
	if (result == null) {
	    return new ArrayList<String>(0);
	}
	return result;
    }

    public Map<String, List<String>> getMappings() {
	return mappings;
    }

    @Override
    public void loadPropertiesFile(InputStream input) {
	super.loadPropertiesFile(input);
	transformProperties();
    }
}
