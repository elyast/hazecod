package org.robotframework.jdiameter;

import static org.junit.Assert.*;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.TemplateApplier;

public class TemplateApplierTest {
    private Document doc;
    private TemplateApplier templateApplier = new TemplateApplier();
    private List<Entry<String, String>> userParameters;

    @Before
    public void setUp() {
	Element root = new Element("CCR");
	doc = new Document(root);

	// element without default value
	Element a = new Element("a");
	a.addAttribute(new Attribute("type", "string"));

	// element with default value
	Element b = new Element("b");
	b.addAttribute(new Attribute("type", "integer"));
	b.addAttribute(new Attribute("value", "5"));

	// element with cardinality
	Element c = new Element("c");
	c.addAttribute(new Attribute("type", "string"));
	c.addAttribute(new Attribute("cardinality", "0..n"));

	// element with cardinality with nested node
	Element d = new Element("d");
	d.addAttribute(new Attribute("type", "integer"));
	d.addAttribute(new Attribute("cardinality", "0..n"));
	Element e = new Element("e");
	e.addAttribute(new Attribute("type", "string"));
	d.appendChild(e);

	root.appendChild(a);
	root.appendChild(b);
	root.appendChild(c);
	root.appendChild(d);
    }

    @Test
    public void testNoUserParameters() {
	userParameters = new LinkedList<Entry<String, String>>();
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 1);
	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));
	assertTrue(b.getAttributeCount() == 2);
	assertTrue(b.getAttributeValue("type").equals("integer"));
	assertTrue(b.getAttributeValue("value").equals("5"));
    }

    @Test
    public void testParameterForNodeWithDefaultValue() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters
		.add(new AbstractMap.SimpleEntry<String, String>("b", "6"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 1);
	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));
	assertTrue(b.getAttributeCount() == 2);
	assertTrue(b.getAttributeValue("type").equals("integer"));
	assertTrue(b.getAttributeValue("value").equals("6"));
    }

    @Test
    public void testParameterForNodeWithoutDefaultValue() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("a",
		"yy"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 2);

	Element a = children.get(0);
	assertTrue(a.getLocalName().equals("a"));
	assertTrue(a.getAttributeCount() == 2);
	assertTrue(a.getAttributeValue("type").equals("string"));
	assertTrue(a.getAttributeValue("value").equals("yy"));

	Element b = children.get(1);
	assertTrue(b.getLocalName().equals("b"));
	assertTrue(b.getAttributeCount() == 2);
	assertTrue(b.getAttributeValue("type").equals("integer"));
	assertTrue(b.getAttributeValue("value").equals("5"));
    }

    @Test
    public void testSingleParameterForNodeWithCardinality() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("c[0]",
		"xx"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 2);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));
	assertTrue(b.getAttributeCount() == 2);
	assertTrue(b.getAttributeValue("type").equals("integer"));
	assertTrue(b.getAttributeValue("value").equals("5"));

	Element c = children.get(1);
	assertTrue(c.getLocalName().equals("c"));
	assertTrue(c.getAttributeCount() == 3);
	assertTrue(c.getAttributeValue("type").equals("string"));
	assertTrue(c.getAttributeValue("value").equals("xx"));
	assertTrue(c.getAttributeValue("cardinality").equals("0..n"));
    }

    @Test
    public void testMultipleParametersForNodeWithCardinality() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("c[0]",
		"xx"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("c[1]",
		"yy"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 3);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));
	assertTrue(b.getAttributeCount() == 2);
	assertTrue(b.getAttributeValue("type").equals("integer"));
	assertTrue(b.getAttributeValue("value").equals("5"));

	Element c1 = children.get(1);
	assertTrue(c1.getLocalName().equals("c"));
	assertTrue(c1.getAttributeCount() == 3);
	assertTrue(c1.getAttributeValue("type").equals("string"));
	assertTrue(c1.getAttributeValue("value").equals("xx"));
	assertTrue(c1.getAttributeValue("cardinality").equals("0..n"));

	Element c2 = children.get(2);
	assertTrue(c2.getLocalName().equals("c"));
	assertTrue(c2.getAttributeCount() == 3);
	assertTrue(c2.getAttributeValue("type").equals("string"));
	assertTrue(c2.getAttributeValue("value").equals("yy"));
	assertTrue(c2.getAttributeValue("cardinality").equals("0..n"));
    }

    @Test
    public void testSingleParameterInNestedNodes() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[0].e", "ee"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 2);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));

	Element d = children.get(1);
	assertTrue(d.getLocalName().equals("d"));
	assertTrue(d.getAttributeCount() == 2);
	assertTrue(d.getAttributeValue("type").equals("integer"));
	assertTrue(d.getAttributeValue("cardinality").equals("0..n"));

	assertTrue(d.getChildElements().size() == 1);
	Element e = d.getChildElements().get(0);
	assertTrue(e.getLocalName().equals("e"));
	assertTrue(e.getAttributeCount() == 2);
	assertTrue(e.getAttributeValue("type").equals("string"));
	assertTrue(e.getAttributeValue("value").equals("ee"));
    }

    @Test
    public void testSingleParameterInNestedNodes2() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("d[0]",
		"10"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 2);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));

	Element d = children.get(1);
	assertTrue(d.getLocalName().equals("d"));
	assertTrue(d.getAttributeCount() == 3);
	assertTrue(d.getAttributeValue("type").equals("integer"));
	assertTrue(d.getAttributeValue("value").equals("10"));
	assertTrue(d.getAttributeValue("cardinality").equals("0..n"));
	assertTrue(d.getChildElements().size() == 0);
    }

    @Test
    public void testMultipleParametersInNestedNodes() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[0].e", "ee0"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[1].e", "ee1"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 3);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));

	Element d1 = children.get(1);
	assertTrue(d1.getLocalName().equals("d"));
	assertTrue(d1.getAttributeCount() == 2);
	assertTrue(d1.getAttributeValue("type").equals("integer"));
	assertTrue(d1.getAttributeValue("cardinality").equals("0..n"));

	assertTrue(d1.getChildElements().size() == 1);
	Element e1 = d1.getChildElements().get(0);
	assertTrue(e1.getLocalName().equals("e"));
	assertTrue(e1.getAttributeCount() == 2);
	assertTrue(e1.getAttributeValue("type").equals("string"));
	assertTrue(e1.getAttributeValue("value").equals("ee0"));

	Element d2 = children.get(2);
	assertTrue(d2.getLocalName().equals("d"));
	assertTrue(d2.getAttributeCount() == 2);
	assertTrue(d2.getAttributeValue("type").equals("integer"));
	assertTrue(d2.getAttributeValue("cardinality").equals("0..n"));

	assertTrue(d2.getChildElements().size() == 1);
	Element e2 = d2.getChildElements().get(0);
	assertTrue(e2.getLocalName().equals("e"));
	assertTrue(e2.getAttributeCount() == 2);
	assertTrue(e2.getAttributeValue("type").equals("string"));
	assertTrue(e2.getAttributeValue("value").equals("ee1"));
    }

    @Test
    public void testMultipleParametersInNestedNodes2() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[0].e", "ee0"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("d[1]",
		"99"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 3);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));

	Element d1 = children.get(1);
	assertTrue(d1.getLocalName().equals("d"));
	assertTrue(d1.getAttributeCount() == 2);
	assertTrue(d1.getAttributeValue("type").equals("integer"));
	assertTrue(d1.getAttributeValue("cardinality").equals("0..n"));

	assertTrue(d1.getChildElements().size() == 1);
	Element e1 = d1.getChildElements().get(0);
	assertTrue(e1.getLocalName().equals("e"));
	assertTrue(e1.getAttributeCount() == 2);
	assertTrue(e1.getAttributeValue("type").equals("string"));
	assertTrue(e1.getAttributeValue("value").equals("ee0"));

	Element d2 = children.get(2);
	assertTrue(d2.getLocalName().equals("d"));
	assertTrue(d2.getAttributeCount() == 3);
	assertTrue(d2.getAttributeValue("type").equals("integer"));
	assertTrue(d2.getAttributeValue("value").equals("99"));
	assertTrue(d2.getAttributeValue("cardinality").equals("0..n"));
	assertTrue(d2.getChildElements().size() == 0);
    }

    @Test
    public void testMultipleParametersInNestedNodes3() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("d[0]",
		"98"));
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[1].e", "ee1"));
	templateApplier.apply(userParameters, doc);

	Element root = doc.getRootElement();
	Elements children = root.getChildElements();
	assertTrue(children.size() == 3);

	Element b = children.get(0);
	assertTrue(b.getLocalName().equals("b"));

	Element d1 = children.get(1);
	assertTrue(d1.getLocalName().equals("d"));
	assertTrue(d1.getAttributeCount() == 3);
	assertTrue(d1.getAttributeValue("type").equals("integer"));
	assertTrue(d1.getAttributeValue("value").equals("98"));
	assertTrue(d1.getAttributeValue("cardinality").equals("0..n"));
	assertTrue(d1.getChildElements().size() == 0);

	Element d2 = children.get(2);
	assertTrue(d2.getLocalName().equals("d"));
	assertTrue(d2.getAttributeCount() == 2);
	assertTrue(d2.getAttributeValue("type").equals("integer"));
	assertTrue(d2.getAttributeValue("cardinality").equals("0..n"));

	assertTrue(d2.getChildElements().size() == 1);
	Element e1 = d2.getChildElements().get(0);
	assertTrue(e1.getLocalName().equals("e"));
	assertTrue(e1.getAttributeCount() == 2);
	assertTrue(e1.getAttributeValue("type").equals("string"));
	assertTrue(e1.getAttributeValue("value").equals("ee1"));
    }

    @Test
    public void testWrongIndexedUserParameter() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>(
		"d[2].e", "ee1"));
	try {
	    templateApplier.apply(userParameters, doc);
	    assertTrue(false);
	} catch (IndexOutOfBoundsException e) {
	    assertTrue(true);
	}

    }

    @Test
    public void testNotExistingUserParameter() {
	userParameters = new LinkedList<Entry<String, String>>();
	userParameters.add(new AbstractMap.SimpleEntry<String, String>("x",
		"xx"));
	try {
	    templateApplier.apply(userParameters, doc);
	    assertTrue(false);
	} catch (IllegalArgumentException e) {
	    assertTrue(true);
	}

    }
}
