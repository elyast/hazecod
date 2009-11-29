package org.robotframework.jdiameter;

import nu.xom.Attribute;
import nu.xom.Element;

public class ElementFactory {

    public static Element createEnumElement(String string, String... vendor) {
	Element e = new Element(string);
	e.addAttribute(new Attribute("type", "enum"));
	e.addAttribute(new Attribute("value", "INITIAL_REQUEST"));
	if (vendor.length == 1) {
	    e.addAttribute(new Attribute("vendor", String.valueOf(vendor[0])));
	}
	return e;
    }

    public static Element createIntElement(String string, String... vendor) {
	Element e = new Element(string);
	e.addAttribute(new Attribute("type", "int"));
	e.addAttribute(new Attribute("value", "123"));
	if (vendor.length == 1) {
	    e.addAttribute(new Attribute("vendor", String.valueOf(vendor[0])));
	}
	return e;
    }

    public static Element createLongElement(String string, String... vendor) {
	return createLongElement(string, false, vendor);
    }

    public static Element createLongElement(String string,
	    boolean asUnsignedInt32, String... vendor) {
	Element e = new Element(string);
	e.addAttribute(new Attribute("type", "long"));
	e.addAttribute(new Attribute("value", "321"));
	e.addAttribute(new Attribute("asUnsignedInt32", String
		.valueOf(asUnsignedInt32)));
	if (vendor.length == 1) {
	    e.addAttribute(new Attribute("vendor", String.valueOf(vendor[0])));
	}

	return e;
    }

    public static Element createFloatElement(String string, String... vendor) {
	Element e = new Element(string);
	e.addAttribute(new Attribute("type", "float"));
	e.addAttribute(new Attribute("value", "123.123"));
	if (vendor.length == 1) {
	    e.addAttribute(new Attribute("vendor", String.valueOf(vendor[0])));
	}

	return e;
    }

    public static Element createStringElement(String string, String... vendor) {
	Element e = new Element(string);
	e.addAttribute(new Attribute("type", "string"));
	e.addAttribute(new Attribute("value", "stringull"));
	if (vendor.length == 1) {
	    e.addAttribute(new Attribute("vendor", String.valueOf(vendor[0])));
	}
	return e;
    }

    public static Element createGroupElement(String... tree) {
	Element parent = new Element(tree[0]);
	parent.addAttribute(new Attribute("type", "string"));
	parent.addAttribute(new Attribute("value", "stringull"));
	Element root = parent;
	for (int i = 1; i < tree.length; i++) {
	    Element e = new Element(tree[i]);
	    e.addAttribute(new Attribute("type", "string"));
	    e.addAttribute(new Attribute("value", "stringull"));
	    if (parent != null) {
		parent.appendChild(e);
	    }
	    parent = e;
	}
	return root;
    }

}
