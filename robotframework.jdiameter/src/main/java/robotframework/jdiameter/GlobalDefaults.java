package robotframework.jdiameter;

public class GlobalDefaults extends CodeResolver {

    public GlobalDefaults() {
	super("diameter.properties");
    }

    public int getDefaultEndToEndId() {
	return getCode("endToEndId");
    }

    public int getDefaultApplicationId() {
	return getCode("applicationId");
    }

    public int getDefaultHopByHopId() {
	return getCode("hopByHopId");
    }

    public String getDefaultHost() {
	return props.getProperty("host");
    }

    public int getDefaultPort() {
	return getCode("port");
    }

    public long getDefaultTimeout() {
	return Long.parseLong(props.getProperty("timeout").trim());
    }

    public int getCommandCode(String localName) {
	return getCode(localName);
    }

    public int getVendorId(String vendorText) {
	return getCode(vendorText);
    }

}
