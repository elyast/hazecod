package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.mapper.AvpCodeResolver;
import org.robotframework.jdiameter.mapper.AvpEnumResolver;
import org.robotframework.jdiameter.mapper.GlobalDefaults;

public class DiameterCodecTest {

    private DiameterCodec testObj;

    @Before
    public void setup() throws IllegalDiameterStateException,
	    InternalException, Exception {
	testObj = new DiameterCodec();
	testObj.setGlobalDefaults(new GlobalDefaults());
	testObj.setAvpCodesResolver(new AvpCodeResolver());
	testObj.setAvpEnumsResolver(new AvpEnumResolver());
	StackImpl stackImpl = new StackImpl();
	InputStream istream = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("configuration.xml");
	SessionFactory init = stackImpl.init(new XMLConfiguration(istream));
	Session session = init.getNewSession();
	testObj.session = session;
	testObj.lastRequest = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");
    }

    @Test
    public void testEncode_NoChildren() throws Exception {
	Element root = new Element("CREDIT_CONTROL_REQUEST");
	root.addAttribute(new Attribute("applicationId", "4"));
	root.addAttribute(new Attribute("endToEndId", "19"));
	root.addAttribute(new Attribute("hopByHopId", "37"));
	Document doc = new Document(root);
	Message msg = testObj.encode(doc);
	assertEquals(272, msg.getCommandCode());
	// assertEquals(0, msg.getAvps().size());
    }

    @Test
    public void testEncode_Flat_DifferentTypes() throws Exception {
	Element root = new Element("CREDIT_CONTROL_REQUEST");
	root.addAttribute(new Attribute("applicationId", "4"));
	root.addAttribute(new Attribute("endToEndId", "19"));
	root.addAttribute(new Attribute("hopByHopId", "37"));
	Document doc = new Document(root);

	root.appendChild(ElementFactory.createIntElement("SERVICE_IDENTIFIER"));// 439
	root.appendChild(ElementFactory.createEnumElement("RESULT_CODE"));// 272
	root.appendChild(ElementFactory.createLongElement("CC_REQUEST_NUMBER",
		true));// 415
	root.appendChild(ElementFactory.createStringElement("SESSION_ID"));// 263
	root.appendChild(ElementFactory.createStringElement("GPP_ADDRESS_DATA",
		"3GPP"));// 897

	Message msg = testObj.encode(doc);
	assertEquals(272, msg.getCommandCode());
	AvpSet avps = msg.getAvps();
	// assertEquals(5, avps.size());
	Avp avp = avps.getAvp(263);
	assertNotNull(avp);
	assertNotNull(avp.getUTF8String());

	avp = avps.getAvp(268);
	assertNotNull(avp);
	assertEquals(1, avp.getInteger32());

	avp = avps.getAvp(415);
	assertNotNull(avp);
	long unsigned32 = avp.getUnsigned32();
	assertEquals(321L, unsigned32);

	avp = avps.getAvp(439);
	assertNotNull(avp);
	assertEquals(123, avp.getInteger32());

	avp = avps.getAvp(897);
	assertNotNull(avp);
	assertEquals("stringull", avp.getUTF8String());
	assertEquals(10415, avp.getVendorId());

    }

    @Test
    public void testEncodeNested_elements() throws Exception {
	Element root = new Element("CREDIT_CONTROL_REQUEST");
	root.addAttribute(new Attribute("applicationId", "4"));
	root.addAttribute(new Attribute("endToEndId", "19"));
	root.addAttribute(new Attribute("hopByHopId", "37"));
	Document doc = new Document(root);

	root.appendChild(ElementFactory.createGroupElement(
		"GPP_MMS_INFORMATION", "GPP_ADDRESS_DATA"));// 877
	root.appendChild(ElementFactory.createGroupElement(
		"GPP_SERVICE_INFORMATION", "GPP_ADDRESS_DATA"));// 873
	root.appendChild(ElementFactory.createStringElement("SESSION_ID"));// 263

	Message msg = testObj.encode(doc);
	assertEquals(272, msg.getCommandCode());
	AvpSet avps = msg.getAvps();

	// assertEquals(3, avps.size());
	Avp avp = avps.getAvp(263);
	assertNotNull(avp);
	assertNotNull(avp.getUTF8String());

	avp = avps.getAvp(873);
	assertNotNull(avp);
	AvpSet group = avp.getGrouped();
	assertEquals(1, group.size());
	assertEquals(897, group.getAvpByIndex(0).getCode());
	assertEquals("stringull", group.getAvpByIndex(0).getUTF8String());

	avp = avps.getAvp(877);
	assertNotNull(avp);
	group = avp.getGrouped();
	assertEquals(1, group.size());
	assertEquals(897, group.getAvpByIndex(0).getCode());
	assertEquals("stringull", group.getAvpByIndex(0).getUTF8String());
    }

    @Test
    public void testEncode_VendorHierarchy() throws Exception {
	Element root = new Element("CREDIT_CONTROL_REQUEST");
	root.addAttribute(new Attribute("applicationId", "4"));
	root.addAttribute(new Attribute("endToEndId", "19"));
	root.addAttribute(new Attribute("hopByHopId", "37"));
	Document doc = new Document(root);

	Element mms = ElementFactory.createGroupElement("GPP_MMS_INFORMATION",
		"GPP_ADDRESS_DATA");
	root.appendChild(mms);// 877
	mms.addAttribute(new Attribute("vendor", "3GPP"));

	Message msg = testObj.encode(doc);
	assertEquals(272, msg.getCommandCode());
	AvpSet avps = msg.getAvps();

	// assertEquals(1, avps.size());

	Avp avp = avps.getAvp(877);
	assertEquals(10415, avp.getVendorId());
	assertNotNull(avp);
	AvpSet group = avp.getGrouped();
	assertEquals(1, group.size());
	assertEquals(897, group.getAvpByIndex(0).getCode());
	assertEquals(10415, group.getAvpByIndex(0).getVendorId());
	assertEquals("stringull", group.getAvpByIndex(0).getUTF8String());
    }

}
