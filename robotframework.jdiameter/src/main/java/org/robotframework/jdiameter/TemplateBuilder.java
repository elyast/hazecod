package org.robotframework.jdiameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Document;

import org.robotframework.jdiameter.mapper.MappingReader;
import org.robotframework.protocol.TemplateProcessor;

/**
 * Applies user parameters to xml template
 * 
 */
public class TemplateBuilder implements TemplateProcessor {

    private static final String CUSTOM_TEMPLATE = "Custom";

    private static final String XML = ".xml";

    private static final String PROPS = ".properties";

    /**
     * Read xml template
     */
    TemplateReader templateReader;
    /**
     * Read additional aliases to xml elements
     */
    MappingReader mappingReader;
    /**
     * Parses user parameters
     */
    UserParameterParser userParamParser;
    /**
     * Applies aliases to user parameters
     */
    UserParameterTransformer userParamTransformer;
    /**
     * Applies user parameters to choosen xml template
     */
    TemplateApplier templateApplier;

    /**
     * Builds from user parameter two xml document that represents diameter
     * message
     * 
     * @param template Template name (or path without extension)
     * @param params user parameters to override template defaults
     * @return Xml Document after overriding template defaults
     */
    public Document processTemplate(String template, String[] params) {

	Template path = getPath(template);
	Document doc = templateReader.read(path.xmlIn);
	mappingReader.loadPropertiesFile(path.propIn);

	Map<String, List<String>> mapping = mappingReader.getMappings();

	List<String> parameters = new ArrayList<String>(Arrays.asList(params));

	List<Entry<String, String>> userParameters = userParamParser
		.parse(parameters);
	List<Entry<String, String>> qualifiedUserParameters = 
	    userParamTransformer
		.expandUserParametersWithAliases(mapping, userParameters);

	templateApplier.apply(qualifiedUserParameters, doc);
	return doc;
    }
    
    /**
     * @param applier Apply user parameters on template
     */
    public void setTemplateApplier(TemplateApplier applier) {
	this.templateApplier = applier;
    }

    /**
     * @param userParamParser User parameters parser
     */
    public void setUserParamParser(UserParameterParser userParamParser) {
	this.userParamParser = userParamParser;
    }

    /**
     * @param userParamTransform Expand user parameters
     */
    public void setUserParamTransformer(
	    UserParameterTransformer userParamTransform) {
	this.userParamTransformer = userParamTransform;
    }

    /**
     * @param xmlReader XML template reader
     */
    public void setTemplateReader(TemplateReader xmlReader) {
	this.templateReader = xmlReader;
    }

    /**
     * @param mappingReader Mapping reader file
     */
    public void setMappingReader(MappingReader mappingReader) {
	this.mappingReader = mappingReader;
    }    

    /**
     * Gets xml templated file (from predefined storage or custom one)
     * 
     * @param param
     * @return
     */
    Template getPath(String param) {
	String[] splitted = param.split("=");
	if (splitted.length == 2) {
	    if (CUSTOM_TEMPLATE.equals(splitted[0].trim())) {
		return new Template(new File(splitted[1].trim() + XML),
			new File(splitted[1].trim() + PROPS));
	    }
	    throw new RuntimeException(
		    "Custom format: Custom=file without extension");
	}
	if (splitted.length == 1) {
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    return new Template(cl
		    .getResourceAsStream(splitted[0].trim() + XML), cl
		    .getResourceAsStream(splitted[0].trim() + PROPS));
	}
	throw new RuntimeException(
		"Invalid sytanx of template file: [predefine template name]"
		+ " | [Custom=file without extension]");
    }

    /**
     * 
     * Diameter message template consists of xml template file and additional
     * aliases
     * 
     * @author Eliot
     * 
     */
    static class Template {

	InputStream xmlIn;
	InputStream propIn;

	public Template(File xml, File props) {
	    try {
		xmlIn = new FileInputStream(xml);
		propIn = new FileInputStream(props);
	    } catch (FileNotFoundException e) {
		throw new RuntimeException(e);
	    }
	}

	public Template(InputStream xmlIn, InputStream propIn) {
	    this.xmlIn = xmlIn;
	    this.propIn = propIn;
	}

    }

}
