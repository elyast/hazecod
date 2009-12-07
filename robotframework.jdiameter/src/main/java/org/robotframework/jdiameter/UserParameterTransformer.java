package org.robotframework.jdiameter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Resolves aliases names into full qualified xml element names
 * 
 * @author Eliot
 * 
 */
public class UserParameterTransformer {

    /**
     * Resolves aliases names into full qualified xml element names
     * 
     * @param mapping
     * @param userParameters
     * @return
     */
    public List<Entry<String, String>> expandUserParametersWithAliases(
	    Map<String, List<String>> mapping,
	    List<Entry<String, String>> userParameters) {

	List<Entry<String, String>> result = new ArrayList<Entry<String, String>>();
	if (userParameters == null) {
	    return result;
	}
	for (Entry<String, String> entry : userParameters) {
	    result.addAll(expand(entry, mapping));
	}
	return result;
    }

    private List<Entry<String, String>> expand(Entry<String, String> entry,
	    Map<String, List<String>> mapping) {

	List<Entry<String, String>> result = new ArrayList<Entry<String, String>>();
	List<String> mappedBy = mapping.get(entry.getKey());
	if (mappedBy != null) {
	    for (String object : mappedBy) {
		result.add(new AbstractMap.SimpleEntry<String, String>(object,
			entry.getValue()));
	    }
	} else {
	    result.add(new AbstractMap.SimpleEntry<String, String>(entry
		    .getKey(), entry.getValue()));
	}
	return result;
    }

}
