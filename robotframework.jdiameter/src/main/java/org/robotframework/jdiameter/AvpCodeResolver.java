package org.robotframework.jdiameter;

/**
 * Helper used to retrieve AVP Codes, codes are read from properties file 
 */
public class AvpCodeResolver extends CodeResolver {

    private static final String AVPCODES_PROPERTIES = "avpcodes.properties";

    public AvpCodeResolver() {
	super(AVPCODES_PROPERTIES);
    }

}
