package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;

import nu.xom.Document;

import org.junit.Before;
import org.junit.Test;

public class TemplateReaderTest {

    private TemplateReader testObj;

    @Before
    public void setup() {
	testObj = new TemplateReader();
    }

    @Test
    public void testPredefined() throws Exception {
	Document doc = testObj.read(ClassLoader
		.getSystemResourceAsStream("MMS-IEC-CCR.xml"));
	assertNotNull(doc);
	assertEquals("CREDIT_CONTROL_REQUEST", doc.getRootElement()
		.getLocalName());

	doc = testObj.read(ClassLoader
		.getSystemResourceAsStream("MMS-IEC-CCA.xml"));
	assertNotNull(doc);
	assertEquals("CREDIT_CONTROL_ANSWER", doc.getRootElement()
		.getLocalName());

	doc = testObj.read(ClassLoader
		.getSystemResourceAsStream("MMS-ECUR-CCR.xml"));
	assertNotNull(doc);
	assertEquals("CREDIT_CONTROL_REQUEST", doc.getRootElement()
		.getLocalName());

	doc = testObj.read(ClassLoader
		.getSystemResourceAsStream("MMS-ECUR-CCA.xml"));
	assertNotNull(doc);
	assertEquals("CREDIT_CONTROL_ANSWER", doc.getRootElement()
		.getLocalName());
    }

    @Test
    public void testCustom() throws Exception {
	File file = new File(ClassLoader.getSystemResource("MMS-IEC-CCR.xml")
		.getFile());
	Document doc = testObj.read(new FileInputStream(file));
	assertNotNull(doc);
	assertEquals("CREDIT_CONTROL_REQUEST", doc.getRootElement()
		.getLocalName());
    }
    
    @Test(expected=RuntimeException.class)
    public void testRead_null() throws Exception {
	testObj.read(null);
    }
}
