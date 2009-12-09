package org.robotframework.jdiameter.mapper;

/**
 * Helper used to retrieve global default values
 */
public class GlobalDefaults extends CodeResolver {

    private static final String APPLICATION_ID = "applicationId";
    private static final String DIAMETER_PROPERTIES = "diameter.properties";

    /**
     * 
     */
    public GlobalDefaults() {
	super(DIAMETER_PROPERTIES);
    }

    /**
     * @return Default application id
     */
    public int getDefaultApplicationId() {
	return getCode(APPLICATION_ID);
    }

    /**
     * @param localName command name
     * @return command code
     */
    public int getCommandCode(String localName) {
	return getCode(localName);
    }

    /**
     * @param vendorText Vendor name
     * @return vendor id
     */
    public int getVendorId(String vendorText) {
	return getCode(vendorText);
    }

}
