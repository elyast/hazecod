package org.robotframework.jdiameter.mapper;

/**
 * Helper used to retrieve global default values
 */
public class GlobalDefaults extends CodeResolver {

    private static final String APPLICATION_ID = "applicationId";
    private static final String DIAMETER_PROPERTIES = "diameter.properties";

    public GlobalDefaults() {
	super(DIAMETER_PROPERTIES);
    }

    public int getDefaultApplicationId() {
	return getCode(APPLICATION_ID);
    }

    public int getCommandCode(String localName) {
	return getCode(localName);
    }

    public int getVendorId(String vendorText) {
	return getCode(vendorText);
    }

}
