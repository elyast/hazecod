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

/**
 * Applies user parameters to xml template
 * 
 */
public class TemplateBuilder {

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
     * @param params
     * @param defaultApplicationId
     * @return
     */
    public Document build(Object[] params, int defaultApplicationId) {

	Object templateParam = params[0];
	Template path = getPath(String.valueOf(templateParam));
	Document doc = templateReader.read(path.xmlIn);
	mappingReader.loadPropertiesFile(path.propIn);

	Map<String, List<String>> mapping = mappingReader.getMappings();

	List<Object> parameters = new ArrayList<Object>(Arrays.asList(params));
	parameters.remove(0);

	List<Entry<String, String>> userParameters = userParamParser
		.parse(parameters);
	List<Entry<String, String>> qualifiedUserParameters = userParamTransformer
		.expandUserParametersWithAliases(mapping, userParameters);

	templateApplier.apply(qualifiedUserParameters, doc);
	return doc;
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
	    return new Template(ClassLoader
		    .getSystemResourceAsStream(splitted[0].trim() + XML),
		    ClassLoader.getSystemResourceAsStream(splitted[0].trim()
			    + PROPS));
	}
	throw new RuntimeException(
		"Invalid sytanx of template file: [predefine template name] | [Custom=file without extension]");
    }

    public void setTemplateApplier(TemplateApplier applier) {
	this.templateApplier = applier;
    }

    public void setUserParamParser(UserParameterParser userParamParser) {
	this.userParamParser = userParamParser;
    }

    public void setUserParamTransformer(
	    UserParameterTransformer userParamTransform) {
	this.userParamTransformer = userParamTransform;
    }

    public void setTemplateReader(TemplateReader xmlReader) {
	this.templateReader = xmlReader;
    }

    public void setMappingReader(MappingReader mappingReader) {
	this.mappingReader = mappingReader;
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
