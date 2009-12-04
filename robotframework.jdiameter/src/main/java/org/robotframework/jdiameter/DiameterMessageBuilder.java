package org.robotframework.jdiameter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.robotframework.jdiameter.mapper.AvpCodeResolver;
import org.robotframework.jdiameter.mapper.AvpEnumResolver;
import org.robotframework.jdiameter.mapper.GlobalDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds diameter message on xml document (template + parameters applied)
 * 
 * @author Eliot
 * 
 */
public class DiameterMessageBuilder implements ProtocolCodec{

    private static final String APPLICATION_ID = "applicationId";
    private static final String TIMEZONE_ID_UTC = "UTC";
    private static final String HEX_PREFIX = "0x";
    private static final int DEFAULT_VENDOR = 0;
    private static final String DEFAULT_START_TIME = "1900-01-01 00:00:00";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String REALM = "eliot.org";
    private static final String REQUEST_SUFFIX = "REQUEST";
    private static final String TYPE = "type";
    private static final String UNSIGNED_INT32 = "asUnsignedInt32";
    private static final String VALUE = "value";
    private static final String VENDOR = "vendor";
    private static final String INT = "int";
    private static final String STRING = "string";
    private static final String LONG = "long";
    private static final String ENUM = "enum";
    private static final Object TIME = "time";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * global defaults
     */
    GlobalDefaults global;
    /**
     * avp mnemonic name -> avp code
     */
    AvpCodeResolver codes;
    /**
     * avp enum mnemonic -> int
     */
    AvpEnumResolver enums;
    /**
     * JDiameter client session, diameter message factory
     */
    Session session;
    /**
     * Last request (allows creates answer on this request)
     */
    Request lastRequest;
    /**
     * Allows user to input in following format yyyy-MM-dd HH:mm:ss
     */
    DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    /**
     * Start time according to diameter specification Used in time conversion
     */
    long startTime;
    {
	dateFormatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID_UTC));
	try {
	    startTime = dateFormatter.parse(DEFAULT_START_TIME).getTime();
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * encodes Diameter Message according to given xml doc, which is processed,
     * for each element proper Avp is created and added to Message
     * 
     * @param doc
     * @return
     */
    public Message encode(Document doc) {
	Element root = doc.getRootElement();
	int commandCode = global.getCommandCode(root.getLocalName());
	int applicationId = Integer.parseInt(root
		.getAttributeValue(APPLICATION_ID));
	Message msg = null;
	if (root.getLocalName().endsWith(REQUEST_SUFFIX)) {
	    msg = session.createRequest(commandCode, ApplicationId
		    .createByAccAppId(applicationId), REALM);
	} else {
	    msg = lastRequest.createAnswer(0);
	}

	processElements(root.getChildElements(), msg.getAvps());
	return msg;
    }

    void processElements(Elements children, AvpSet avps) {
	processElements(children, null, avps);
    }

    /**
     * Go over xml children element
     * 
     * @param children
     * @param vendor
     * @param avps
     */
    void processElements(Elements children, Integer vendor, AvpSet avps) {
	for (int i = 0; i < children.size(); i++) {
	    processElement(children.get(i), vendor, avps);
	}
    }

    /**
     * Prints the properties of element and all child elements if represents
     * groupedAvp, modifies the given AvpSet and adds avp according to its
     * properties (name, type, value, vendorId etc.)
     * 
     * @param element
     *            processed element representing Avp
     * @param vendorId
     * @param avps
     *            AvpSet which contains all message Avps,
     */
    void processElement(Element element, Integer vendorId, AvpSet avps) {

	ElementProperties elementProperties = new ElementProperties(element);
	Integer vendor = calculateVendor(vendorId, elementProperties
		.getVendorText());

	if (element.getChildElements().size() == 0) {
	    handleSimpleAvp(elementProperties, vendor, avps);
	} else {
	    handleGroupedAvp(element, avps, elementProperties.getName(), vendor);
	}

    }

    Integer calculateVendor(Integer vendorId, String vendorText) {
	Integer vendor = (vendorText == null) ? vendorId : new Integer(global
		.getVendorId(vendorText));
	if (vendor == null) {
	    vendor = DEFAULT_VENDOR;
	}
	return vendor;
    }

    void handleGroupedAvp(Element element, AvpSet avps, String name,
	    Integer vendor) {
	logger.info("Grouped avp: " + name);
	AvpSet avpset = avps.addGroupedAvp(codes.getCode(name), vendor, true,
		false);
	processElements(element.getChildElements(), vendor, avpset);
    }

    void handleSimpleAvp(ElementProperties elementProperties, Integer vendor,
	    AvpSet avps) {
	logger.info("Leaf avp: " + elementProperties.getName() + " value: "
		+ elementProperties.getValueText());

	int code = codes.getCode(elementProperties.getName());
	handleAvpsAddedByDiameterApi(avps, code);

	addAvpToSetAccordingToItsType(elementProperties, vendor, avps, code);
    }

    private void addAvpToSetAccordingToItsType(
	    ElementProperties elementProperties, Integer vendor, AvpSet avps,
	    int code) {

	Object value = null;
	String type = elementProperties.getType();
	String valueText = elementProperties.getValueText();

	if (ENUM.equals(type)) {
	    value = enums.getCode(valueText);
	    avps.addAvp(code, (Integer) value, vendor, true, false);
	}

	if (INT.equals(type)) {
	    value = new Integer(valueText);
	    avps.addAvp(code, (Integer) value, vendor, true, false);
	}
	if (STRING.equals(type)) {
	    value = valueText;
	    avps.addAvp(code, (String) value, vendor, true, false, false);
	}
	if (LONG.equals(type)) {
	    value = new Long(valueText);

	    avps.addAvp(code, (Long) value, vendor, true, false,
		    elementProperties.isAsUnsignedInt32());
	}
	if (TIME.equals(type)) {
	    value = convertTime(valueText);
	    avps.addAvp(code, (String) value, vendor, true, false, true);
	}
	if (value == null) {
	    throw new IllegalArgumentException("Avp: "
		    + elementProperties.getName()
		    + " has null or unrecognized type: " + type + " value: "
		    + valueText);
	}
    }

    void handleAvpsAddedByDiameterApi(AvpSet avps, int code) {
	Avp addedByDiameterApi = avps.getAvp(code);
	if (addedByDiameterApi != null) {
	    avps.removeAvp(code);
	}
    }

    /**
     * Converts time to Octet string
     * 
     * @param valueText
     * @return
     */
    private String convertTime(String valueText) {
	try {
	    Date date = dateFormatter.parse(valueText);
	    long time = (date.getTime() - startTime) / 1000;
	    return HEX_PREFIX + Long.toHexString(time);
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }

    public void setAvpCodesResolver(AvpCodeResolver codes) {
	this.codes = codes;
    }

    public void setGlobalDefaults(GlobalDefaults global) {
	this.global = global;
    }

    public void setAvpEnumsResolver(AvpEnumResolver enums) {
	this.enums = enums;
    }

    private class ElementProperties {

	String name;
	String type;
	String valueText;
	String vendorText;
	boolean asUnsignedInt32;

	public ElementProperties(Element element) {
	    name = element.getLocalName();
	    type = element.getAttributeValue(TYPE);
	    valueText = element.getAttributeValue(VALUE);
	    vendorText = element.getAttributeValue(VENDOR);
	    String u32 = element.getAttributeValue(UNSIGNED_INT32);
	    asUnsignedInt32 = Boolean.valueOf(u32);
	}

	public String getName() {
	    return name;
	}

	public String getType() {
	    return type;
	}

	public String getValueText() {
	    return valueText;
	}

	public String getVendorText() {
	    return vendorText;
	}

	public boolean isAsUnsignedInt32() {
	    return asUnsignedInt32;
	}

    }

    public void setSession(Session session) {
	this.session = session;
    }

    public void setLastRequest(Request lastRequest) {
	this.lastRequest = lastRequest;
    }
}
