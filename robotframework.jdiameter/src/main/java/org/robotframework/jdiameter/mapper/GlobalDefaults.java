package org.robotframework.jdiameter.mapper;

/**
 * 
 * Gets global defaults from properties file
 * @author sasnal.net
 *
 */
public class GlobalDefaults extends CodeResolver {

    public GlobalDefaults() {
	super("diameter.properties");
    }

    public int getDefaultApplicationId() {
	return getCode("applicationId");
    }

    public int getCommandCode(String localName) {
	return getCode(localName);
    }

    public int getVendorId(String vendorText) {
	return getCode(vendorText);
    }

}
