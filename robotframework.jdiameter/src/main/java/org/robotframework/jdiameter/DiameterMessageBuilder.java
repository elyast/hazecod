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
public class DiameterMessageBuilder {

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
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Start time according to diameter specification Used in time conversion
     */
    long startTime;
    {
	dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	try {
	    startTime = dateFormatter.parse("1900-01-01 00:00:00").getTime();
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Encodes xml document into diameter message
     * 
     * @param doc
     * @return
     */
    public Message encode(Document doc) {
	Element root = doc.getRootElement();
	int commandCode = global.getCommandCode(root.getLocalName());
	int applicationId = Integer.parseInt(root
		.getAttributeValue("applicationId"));
	Message msg = null;
	if (root.getLocalName().endsWith(REQUEST_SUFFIX)) {
	    msg = session.createRequest(commandCode, ApplicationId
		    .createByAccAppId(applicationId), REALM);
	} else {
	    msg = lastRequest.createAnswer(0);
	}
	process(root.getChildElements(), null, msg.getAvps());

	return msg;
    }

    /**
     * Go over xml children element
     * 
     * @param children
     * @param vendor
     * @param avps
     */
    void process(Elements children, Integer vendor, AvpSet avps) {
	for (int i = 0; i < children.size(); i++) {
	    inspect(children.get(i), vendor, avps);
	}
    }

    /**
     * Transform xml element into AVP
     * 
     * @param element
     * @param vendorId
     * @param avps
     *            AVP factory and container
     */
    void inspect(Element element, Integer vendorId, AvpSet avps) {
	String name = element.getLocalName();

	String type = element.getAttributeValue(TYPE);
	String valueText = element.getAttributeValue(VALUE);
	String vendorText = element.getAttributeValue(VENDOR);
	boolean asUnsignedInt32 = Boolean.valueOf(element
		.getAttributeValue(UNSIGNED_INT32));

	Integer vendor = (vendorText == null) ? vendorId : new Integer(global
		.getVendorId(vendorText));
	if (vendor == null) {
	    vendor = 0;
	}
	// leaf case
	if (element.getChildElements().size() == 0) {
	    handleSimple(name, type, valueText, asUnsignedInt32, vendor, avps);

	} else {
	    AvpSet avpset = avps.addGroupedAvp(codes.getCode(name), vendor,
		    true, false);
	    process(element.getChildElements(), vendor, avpset);
	}

    }

    /**
     * Transform not grouped xml element to AVP
     * 
     * @param name
     *            Name of AVP
     * @param type
     *            Type of AVP
     * @param valueText
     *            Value in text format
     * @param asUnsignedInt32
     *            If long to be treated like unsignedInt
     * @param vendor
     *            AVP vendor
     * @param avps
     *            AVP container
     */
    void handleSimple(String name, String type, String valueText,
	    boolean asUnsignedInt32, Integer vendor, AvpSet avps) {
	logger.info("Leaf avp: " + name + " value: " + valueText);
	Object value = null;
	if (ENUM.equals(type)) {
	    value = enums.getCode(valueText);
	}
	int code = codes.getCode(name);
	Avp addedByDiameterApi = avps.getAvp(code);
	if (addedByDiameterApi != null) {
	    avps.removeAvp(code);
	}

	if (ENUM.equals(type)) {
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
		    asUnsignedInt32);
	}
	if (TIME.equals(type)) {
	    value = convertTime(valueText);
	    avps.addAvp(code, (String) value, vendor, true, false, true);
	}
	if (value == null) {
	    throw new IllegalArgumentException("Avp: " + name
		    + " has null or unrecognized type: " + type + " value: "
		    + valueText);
	}
    }

    /**
     * Converts time to Octet string
     * @param valueText
     * @return
     */
    private String convertTime(String valueText) {
	try {
	    Date date = dateFormatter.parse(valueText);
	    long time = (date.getTime() - startTime) / 1000;
	    return "0x" + Long.toHexString(time);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return valueText;
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

    public void setSession(Session session) {
	this.session = session;
    }

    public void setLastRequest(Request lastRequest) {
	this.lastRequest = lastRequest;
    }

}
