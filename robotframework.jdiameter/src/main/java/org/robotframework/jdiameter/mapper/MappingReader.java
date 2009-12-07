package org.robotframework.jdiameter.mapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper used to read and transform mapping files from InputStream
 */
public class MappingReader extends PropertiesReader {

    private Map<String, List<String>> mappings = new HashMap<String, List<String>>();

    private void transformProperties() {
	for (String key : props.stringPropertyNames()) {
	    transformProperty(key);
	}
    }

    private void transformProperty(String key) {
	List<String> values = new ArrayList<String>();
	for (String value : props.getProperty(key).split(",")) {
	    if (!value.isEmpty()) {
		values.add(value.trim());
	    }
	}
	mappings.put(key, values);
    }

    /**
     * retrieves earlier prepared mapping for a given key
     * 
     * @param key
     * @return
     */
    public List<String> getMapping(String key) {
	List<String> result = mappings.get(key);
	if (result == null) {
	    return new ArrayList<String>(0);
	}
	return result;
    }

    /**
     * retrieves earlier prepared mappings loaded from InputStream
     * 
     * @return
     */
    public Map<String, List<String>> getMappings() {
	return mappings;
    }

    @Override
    public void loadPropertiesFile(InputStream input) {
	super.loadPropertiesFile(input);
	transformProperties();
    }
}
