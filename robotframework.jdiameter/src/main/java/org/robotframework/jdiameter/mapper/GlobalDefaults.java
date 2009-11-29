package org.robotframework.jdiameter.mapper;

/**
 * Helper used to retrieve global default values
 */
public class GlobalDefaults extends CodeResolver {

    private static final String TIMEOUT = "timeout";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String HOP_BY_HOP_ID = "hopByHopId";
    private static final String APPLICATION_ID = "applicationId";
    private static final String END_TO_END_ID = "endToEndId";
    private static final String DIAMETER_PROPERTIES = "diameter.properties";

    public GlobalDefaults() {
	super(DIAMETER_PROPERTIES);
    }

    public int getDefaultEndToEndId() {
	return getCode(END_TO_END_ID);
    }

    public int getDefaultApplicationId() {
	return getCode(APPLICATION_ID);
    }

    public int getDefaultHopByHopId() {
	return getCode(HOP_BY_HOP_ID);
    }

    public String getDefaultHost() {
	return props.getProperty(HOST);
    }

    public int getDefaultPort() {
	return getCode(PORT);
    }

    public long getDefaultTimeout() {
	return Long.parseLong(props.getProperty(TIMEOUT).trim());
    }

    public int getCommandCode(String localName) {
	return getCode(localName);
    }

    public int getVendorId(String vendorText) {
	return getCode(vendorText);
    }

}
