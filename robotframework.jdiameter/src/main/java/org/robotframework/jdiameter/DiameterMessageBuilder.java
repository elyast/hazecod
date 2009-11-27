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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiameterMessageBuilder {

    private static final String REQUEST_SUFFIX = "REQUEST";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String VENDOR = "vendor";
    private static final String INT = "int";
    private static final String STRING = "string";
    private static final String LONG = "long";
    private static final String ENUM = "enum";
    private static final Object TIME = "time";

    private Logger logger = LoggerFactory.getLogger(getClass());

    GlobalDefaults global;
    AvpCodeResolver codes;
    AvpEnumResolver enums;
    Session session;
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long startTime;
    Request request;
    {
	dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	try {
	    startTime = dateFormatter.parse("1900-01-01 00:00:00").getTime();
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }

    public Message encode(Document doc) {
	Element root = doc.getRootElement();
	int commandCode = global.getCommandCode(root.getLocalName());
	int applicationId = Integer.parseInt(root
		.getAttributeValue("applicationId"));
	Message msg = null;
	if (root.getLocalName().endsWith(REQUEST_SUFFIX)) {
	    msg = session.createRequest(commandCode, ApplicationId
		    .createByAccAppId(applicationId), "eliot.org");
	} else {
	    msg = request.createAnswer(0);
	}
	process(root.getChildElements(), null, msg.getAvps());

	// AvpSet avps = msg.getAvps();
	// for (Avp avp : codes) {
	// Avp f1 = avps.getAvp(avp.getCode());
	// if (f1 != null) {
	// avps.removeAvp(avp.getCode());
	// }
	// avps.addAvp(avp);
	// }
	return msg;
    }

    void process(Elements children, Integer vendor, AvpSet avps) {
	for (int i = 0; i < children.size(); i++) {
	    inspect(children.get(i), vendor, avps);
	}
    }

    // Print the properties of each element
    void inspect(Element element, Integer vendorId, AvpSet avps) {
	String name = element.getLocalName();

	String type = element.getAttributeValue(TYPE);
	String valueText = element.getAttributeValue(VALUE);
	String vendorText = element.getAttributeValue(VENDOR);

	Integer vendor = (vendorText == null) ? vendorId : new Integer(global
		.getVendorId(vendorText));

	// leaf case
	if (element.getChildElements().size() == 0) {
	    handleSimple(name, type, valueText, vendor, avps);

	} else {
	    logger.info("Grouped avp: " + name);
	    AvpSet avpset = null;
	    if (vendor == null) {
		vendor = 0;
	    }
	    avpset = avps.addGroupedAvp(codes.getCode(name), vendor, true,
		    false);
	    process(element.getChildElements(), vendor, avpset);
	}

    }

    void handleSimple(String name, String type, String valueText,
	    Integer vendor, AvpSet avps) {
	logger.info("Leaf avp: " + name + " value: " + valueText);
	Object value = null;
	if (ENUM.equals(type)) {
	    value = enums.getCode(valueText);
	}
	if (vendor == null) {
	    vendor = 0;
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
	    avps.addAvp(code, (Long) value, vendor, true, false);
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

}
