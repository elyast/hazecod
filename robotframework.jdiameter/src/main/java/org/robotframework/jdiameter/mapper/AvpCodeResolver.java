package org.robotframework.jdiameter.mapper;

import java.util.Set;

/**
 * 
 * Avp mnemonic name -> avp code number mapping
 * 
 * @author Eliot
 * 
 */
public class AvpCodeResolver extends CodeResolver {

    private static final String AVPCODES_PROPERTIES = "avpcodes.properties";
    private static final String UNKNOWN = "Unknown";

    /**
     * 
     */
    public AvpCodeResolver() {
	super(AVPCODES_PROPERTIES);
    }

    /**
     * @param code AVPCode
     * @return Name of the code
     */
    public String getName(int code) {
	Set<String> keys = props.stringPropertyNames();
	String codeString = String.valueOf(code);
	for (String string : keys) {	    
	    if (codeString.equals(props.getProperty(string))) {
		return string;
	    }
	}
	return UNKNOWN;
    }

}
