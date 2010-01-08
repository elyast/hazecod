package org.robotframework.jdiameter.mapper;

/**
 * 
 * Avp enum -> avp int mapping
 * 
 * @author sasnal.net
 * 
 */
public class AvpEnumResolver extends CodeResolver {

    private static final String ENUM_PROPERTIES = "enum.properties";

    /**
     * 
     */
    public AvpEnumResolver() {
	super(ENUM_PROPERTIES);
    }

}
