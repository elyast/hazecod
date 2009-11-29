package org.robotframework.jdiameter.mapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Resolves additional aliases to xml template
 * 
 * @author wro02896
 * 
 */
public class MappingReader extends PropertiesReader {

    private Map<String, List<String>> mappings = new HashMap<String, List<String>>();

    public MappingReader() {
    }

    private void transformProperties() {
	for (String key : props.stringPropertyNames()) {
	    mappings.put(key, parseAliases(key));
	}
    }

    private List<String> parseAliases(String key) {
	List<String> values = new ArrayList<String>();
	String[] aliases = props.getProperty(key).split(",");
	for (String value : aliases) {
	    if (!value.isEmpty()) {
		values.add(value.trim());
	    }
	}
	return values;
    }

    /**
     * Gets xml temaplate node from used alias
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
     * Gets all the aliases two xml nodes
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
