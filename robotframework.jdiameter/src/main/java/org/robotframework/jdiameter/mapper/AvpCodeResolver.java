package org.robotframework.jdiameter.mapper;

/**
 * 
 * Avp mnemonic name -> avp code number mapping
 * 
 * @author Eliot
 * 
 */
public class AvpCodeResolver extends CodeResolver {

    private static final String AVPCODES_PROPERTIES = "avpcodes.properties";

    /**
     * 
     */
    public AvpCodeResolver() {
	super(AVPCODES_PROPERTIES);
    }

}
