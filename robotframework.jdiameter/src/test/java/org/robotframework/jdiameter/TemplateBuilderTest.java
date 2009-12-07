package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.jdiameter.TemplateBuilder.Template;
import org.robotframework.jdiameter.mapper.MappingReader;

@RunWith(JMockit.class)
public class TemplateBuilderTest {

    private TemplateBuilder testObj;

    @Mocked
    TemplateReader xmlReader;
    @Mocked
    UserParameterParser userParamParser;
    @Mocked
    UserParameterTransformer userParamTransform;
    @Mocked
    TemplateApplier applier;
    @Mocked
    MappingReader mappingReader;

    @Before
    public void setup() {
	testObj = new TemplateBuilder();
	testObj.setTemplateReader(xmlReader);
	testObj.setTemplateApplier(applier);
	testObj.setUserParamTransformer(userParamTransform);
	testObj.setUserParamParser(userParamParser);
	testObj.setMappingReader(mappingReader);
    }

    @Test
    public void testBuild_Simple() {
	new mockit.Expectations() {
	    {
		xmlReader.read(withInstanceOf(InputStream.class));
		Element root = new Element("CREDIT_CONTROL_REQUEST");
		root.addAttribute(new Attribute("applicationId", "4"));
		root.addAttribute(new Attribute("endToEndId", "37"));
		root.addAttribute(new Attribute("hopByHopId", "19"));
		Document doc = new Document(root);
		returns(doc);
		mappingReader
			.loadPropertiesFile(withInstanceOf(InputStream.class));
		mappingReader.getMappings();

		Map<String, List<String>> mapping = new HashMap<String, List<String>>();
		returns(mapping);
		List<String> params = new ArrayList<String>();
		params.add("SessId=123");
		userParamParser.parse(params);
		List<Entry<String, String>> parsed = new ArrayList<Entry<String, String>>();
		parsed.add(new AbstractMap.SimpleEntry<String, String>(
			"SessId", "123"));
		returns(parsed);
		userParamTransform.expandUserParametersWithAliases(mapping,
			parsed);
		returns(parsed);
		applier.apply(parsed, doc);
	    }
	};
	Document doc = testObj.processTemplate("MMS-IEC-CCR",
		new String[] { "SessId=123" });
	assertEquals(0, doc.getRootElement().getChildCount());
	assertEquals("CREDIT_CONTROL_REQUEST", doc.getRootElement()
		.getLocalName());
    }

    @Test
    public void testGetPath_Custom() throws Exception {
	String file = ClassLoader.getSystemResource("MMS-IEC-CCR.xml")
		.getFile();
	Template tmp = testObj.getPath("Custom="
		+ file.substring(0, file.length() - 4));
	assertNotNull(tmp.xmlIn);
	assertNotNull(tmp.propIn);
    }

    @Test
    public void testGetPath_Predefined() throws Exception {
	Template tmp = testObj.getPath("MMS-IEC-CCR");
	assertNotNull(tmp.xmlIn);
	assertNotNull(tmp.propIn);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetPath_parameterNameError() throws Exception {
	testObj.getPath("name=value");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetPath_syntaxError() throws Exception {
	testObj.getPath("name=value1=value2");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetPath_notExistingFileName() throws Exception {
	testObj.getPath("Custom=newFile.xml");
    }
}
