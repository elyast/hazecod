package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.AvpCodeResolver;
import org.robotframework.jdiameter.AvpEnumResolver;
import org.robotframework.jdiameter.DiameterMessageBuilder;
import org.robotframework.jdiameter.GlobalDefaults;

public class DiameterMessageBuilderTest {

    private DiameterMessageBuilder testObj;

    @Before
    public void setup() {
	testObj = new DiameterMessageBuilder();
	testObj.setGlobalDefaults(new GlobalDefaults());
	testObj.setAvpCodesResolver(new AvpCodeResolver());
	testObj.setAvpEnumsResolver(new AvpEnumResolver());
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
	assertEquals(0, msg.getAvps().size());
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
	root.appendChild(ElementFactory.createLongElement("CC_REQUEST_NUMBER"));// 415
	root.appendChild(ElementFactory.createStringElement("SESSION_ID"));// 263
	root.appendChild(ElementFactory.createStringElement("GPP_ADDRESS_DATA",
		"3GPP"));// 897

	Message msg = testObj.encode(doc);
	assertEquals(272, msg.getCommandCode());
	AvpSet avps = msg.getAvps();
	assertEquals(5, avps.size());
	Avp avp = avps.getAvpByIndex(0);
	assertEquals(263, avp.getCode());
	assertEquals("stringull", avp.getUTF8String());

	avp = avps.getAvpByIndex(1);
	assertEquals(268, avp.getCode());
	assertEquals(1, avp.getInteger32());

	avp = avps.getAvpByIndex(2);
	assertEquals(415, avp.getCode());
	assertEquals(321, avp.getInteger32());

	avp = avps.getAvpByIndex(3);
	assertEquals(439, avp.getCode());
	assertEquals(123, avp.getInteger32());

	avp = avps.getAvpByIndex(4);
	assertEquals(897, avp.getCode());
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

	assertEquals(3, avps.size());
	Avp avp = avps.getAvpByIndex(0);
	assertEquals(263, avp.getCode());
	assertEquals("stringull", avp.getUTF8String());

	avp = avps.getAvpByIndex(1);
	assertEquals(873, avp.getCode());
	AvpSet group = avp.getGrouped();
	assertEquals(1, group.size());
	assertEquals(897, group.getAvpByIndex(0).getCode());
	assertEquals("stringull", group.getAvpByIndex(0).getUTF8String());

	avp = avps.getAvpByIndex(2);
	assertEquals(877, avp.getCode());
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

	assertEquals(1, avps.size());

	Avp avp = avps.getAvpByIndex(0);
	assertEquals(10415, avp.getVendorId());
	assertEquals(877, avp.getCode());
	AvpSet group = avp.getGrouped();
	assertEquals(1, group.size());
	assertEquals(897, group.getAvpByIndex(0).getCode());
	assertEquals(10415, group.getAvpByIndex(0).getVendorId());
	assertEquals("stringull", group.getAvpByIndex(0).getUTF8String());
    }

}
