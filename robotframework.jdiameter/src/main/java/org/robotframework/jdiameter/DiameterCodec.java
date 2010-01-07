package org.robotframework.jdiameter;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
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
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.robotframework.jdiameter.mapper.AvpCodeResolver;
import org.robotframework.jdiameter.mapper.AvpEnumResolver;
import org.robotframework.jdiameter.mapper.GlobalDefaults;
import org.robotframework.protocol.ProtocolCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds diameter message on xml document (template + parameters applied)
 * 
 * @author Eliot
 * 
 */
public class DiameterCodec implements ProtocolCodec {

    static final int SECOND = 1000;
    private static final int DEFAULT_VENDOR = 0;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String REQUEST_SUFFIX = "REQUEST";
    private static final String TYPE = "type";
    private static final String UNSIGNED_INT32 = "asUnsignedInt32";
    private static final String OCTET_STRING = "asOctetString";
    private static final String VALUE = "value";
    private static final String VENDOR = "vendor";
    private static final String INT = "int";
    private static final String URI = "uri";
    private static final String STRING = "string";
    private static final String LONG = "long";
    private static final String ENUM = "enum";
    private static final String ADDRESS = "address";
    private static final String TIME = "time";
    private static final String DOUBLE = "double";
    private static final String FLOAT = "float";
    private static final String DESTINATION_REALM = "destinationRealm";
    private static final String VENDOR_ID = "vendorId";
    private static final String AUTH_APPLICATION_ID = "authApplicationId";
    private static final String ACCT_APPLICATION_ID = "acctApplicationId";

    private static Logger logger = LoggerFactory.getLogger(DiameterCodec.class);

    /**
     * global defaults
     */
    GlobalDefaults globalDefaults;
    /**
     * avp mnemonic name -> avp code
     */
    AvpCodeResolver avpCodesResolver;
    /**
     * avp enum mnemonic -> int
     */
    AvpEnumResolver avpEnumsResolver;
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
    {
	dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * encodes Diameter Message according to given xml doc, which is processed,
     * for each element proper Avp is created and added to Message
     * 
     * @param doc
     *            Applied template with user parameters
     * @return Diameter message build from xml tree
     */
    public Message encode(Document doc) {
	Element root = doc.getRootElement();
	int commandCode = globalDefaults.getCommandCode(root.getLocalName());	
	Message msg = null;
	if (root.getLocalName().endsWith(REQUEST_SUFFIX)) {

	    ApplicationId appId = parseApplicationId(root);
	    String destRealm = root.getAttributeValue(DESTINATION_REALM); 
	    msg = session.createRequest(commandCode, appId, destRealm);
	} else {
	    msg = lastRequest.createAnswer(ResultCode.SUCCESS);
	    AvpSet set = msg.getAvps();
	    removeAllAVPs(set);
	}

	processElements(root.getChildElements(), msg.getAvps());
	return msg;
    }

    ApplicationId parseApplicationId(Element root) {
	String vendor = root.getAttributeValue(VENDOR_ID);
	String authId = root.getAttributeValue(AUTH_APPLICATION_ID);
	String acctId = root.getAttributeValue(ACCT_APPLICATION_ID);
	if (authId == null) {
	    return ApplicationId.createByAccAppId(
		    Long.parseLong(vendor), Long.parseLong(acctId));
	} else {
	    return ApplicationId.createByAuthAppId(
		    Long.parseLong(vendor), Long.parseLong(authId));
	}
    }

    /**
     * Setter
     * 
     * @param codes
     *            AVP Code resolver
     */
    public void setAvpCodesResolver(AvpCodeResolver codes) {
	this.avpCodesResolver = codes;
    }

    /**
     * Setter
     * 
     * @param global
     *            Defaults resolver
     */
    public void setGlobalDefaults(GlobalDefaults global) {
	this.globalDefaults = global;
    }

    /**
     * Setter
     * 
     * @param enums
     *            AVP enums resolver
     */
    public void setAvpEnumsResolver(AvpEnumResolver enums) {
	this.avpEnumsResolver = enums;
    }

    /**
     * Setter
     * 
     * @param requestFactory
     *            Request factory
     */
    @Override
    public void setSesssion(Object requestFactory) {
	this.session = (Session) requestFactory;
    }

    /**
     * Setter
     * 
     * @param lastRequest
     *            Answer factory
     */
    @Override
    public void setLastRequest(Object lastRequest) {
	this.lastRequest = ((Request) lastRequest);
    }

    void removeAllAVPs(AvpSet set) {
	for (int i = 0; i < set.size();) {
	    set.removeAvpByIndex(0);
	}
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
	    handleGroupedAvp(element, avps, 
		    elementProperties.getName(), vendor);
	}

    }

    Integer calculateVendor(Integer vendorId, String vendorText) {

	Integer vendor = vendorId;
	if (vendorText != null) {
	    vendor = Integer.valueOf(globalDefaults.getVendorId(vendorText));
	}
	if (vendor == null) {
	    vendor = DEFAULT_VENDOR;
	}
	return vendor;
    }

    void handleGroupedAvp(Element element, AvpSet avps, String name,
	    Integer vendor) {
	logger.debug("Grouped avp: " + name);
	AvpSet avpset = avps.addGroupedAvp(avpCodesResolver.getCode(name),
		vendor, true, false);
	processElements(element.getChildElements(), vendor, avpset);
    }

    void handleSimpleAvp(ElementProperties elementProperties, Integer vendor,
	    AvpSet avps) {
	logger.debug("Leaf avp: " + elementProperties.getName() + " value: "
		+ elementProperties.getValueText());

	int code = avpCodesResolver.getCode(elementProperties.getName());
	handleAvpsAddedByDiameterApi(avps, code);

	addAvpToSetAccordingToItsType(elementProperties, vendor, avps, code);
    }

    void handleAvpsAddedByDiameterApi(AvpSet avps, int code) {
	Avp addedByDiameterApi = avps.getAvp(code);
	if (addedByDiameterApi != null) {
	    logger.warn("Already exists avp: " + code);
	    // avps.removeAvp(code);
	}
    }

    private void addAvpToSetAccordingToItsType(
	    ElementProperties elementProperties, Integer vendor, AvpSet avps,
	    int code) {

	Object value = null;
	String type = elementProperties.getType();
	String valueText = elementProperties.getValueText();

	if (ENUM.equals(type)) {
	    value = avpEnumsResolver.getCode(valueText);
	    avps.addAvp(code, (Integer) value, vendor, true, false);
	}

	if (DOUBLE.equals(type)) {
	    value = new Double(valueText);
	    avps.addAvp(code, (Double) value, vendor, true, false);
	}
	
	if (FLOAT.equals(type)) {
	    value = new Float(valueText);
	    avps.addAvp(code, (Float) value, vendor, true, false);
	}
	
	if (ADDRESS.equals(type)) {
	    try {
		value = InetAddress.getByName(valueText);
		avps.addAvp(code, (InetAddress) value, vendor, true, false);
	    } catch (UnknownHostException e) {
		throw new IllegalArgumentException(e);
	    }
	}
	
	if (URI.equals(type)) {
	    try {
		value = new org.jdiameter.api.URI(valueText);
		avps.addAvp(code, (org.jdiameter.api.URI) value, 
			vendor, true, false);
	    } catch (UnknownServiceException e) {
		throw new IllegalArgumentException(e);
	    } catch (URISyntaxException e) {
		throw new IllegalArgumentException(e);
	    }
	}
	
	if (INT.equals(type)) {
	    value = new Integer(valueText);
	    avps.addAvp(code, (Integer) value, vendor, true, false);
	}
	if (STRING.equals(type)) {
	    value = valueText;
	    avps.addAvp(code, (String) value, vendor, true, false, 
		    elementProperties.isAsOctetString());
	}
	if (LONG.equals(type)) {
	    value = new Long(valueText);

	    avps.addAvp(code, (Long) value, vendor, true, false,
		    elementProperties.isAsUnsignedInt32());
	}
	if (TIME.equals(type)) {
	    try {
		value = dateFormatter.parse(valueText);
		avps.addAvp(code, (Date) value, vendor, true, false);
	    } catch (ParseException e) {
		throw new IllegalArgumentException(e);
	    }
	}
	if (value == null) {
	    throw new IllegalArgumentException("Avp: "
		    + elementProperties.getName()
		    + " has null or unrecognized type: " + type + " value: "
		    + valueText);
	}
    }

    static class ElementProperties {

	String name;
	String type;
	String valueText;
	String vendorText;
	boolean asUnsignedInt32;
	boolean asOctetString;

	public ElementProperties(Element element) {
	    name = element.getLocalName();
	    type = element.getAttributeValue(TYPE);
	    valueText = element.getAttributeValue(VALUE);
	    vendorText = element.getAttributeValue(VENDOR);
	    String u32 = element.getAttributeValue(UNSIGNED_INT32);
	    String octet = element.getAttributeValue(OCTET_STRING);
	    asUnsignedInt32 = Boolean.valueOf(u32);
	    asOctetString = Boolean.valueOf(octet);
	}

	public boolean isAsOctetString() {
	    return asOctetString;
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
}
