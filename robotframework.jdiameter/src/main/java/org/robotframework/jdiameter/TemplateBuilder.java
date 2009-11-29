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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 */
public class TemplateBuilder {

    private static final String CUSTOM_TEMPLATE = "Custom";

    private static final String XML = ".xml";

    private static final String PROPS = ".properties";

    TemplateReader templateReader;
    MappingReader mappingReader;
    UserParameterParser userParamParser;
    UserParameterTransformer userParamTransformer;
    TemplateApplier templateApplier;
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param params
     * @param defaultApplicationId
     * @param defaultEndToEndId
     * @param defaultHopByHopId
     * @return
     */
    public Document build(String template, String[] params,
	    int defaultApplicationId, int defaultEndToEndId,
	    int defaultHopByHopId) {

	Template path = getPath(template);
	Document doc = templateReader.read(path.xmlIn);
	mappingReader.loadPropertiesFile(path.propIn);
	Map<String, List<String>> mapping = mappingReader.getMappings();
	logger.info("Mappings: " + mapping);

	List<String> parameters = new ArrayList<String>(Arrays.asList(params));

	List<Entry<String, String>> qualifiedUserParameters = userParamTransformer
		.transform(mapping, userParamParser.parse(parameters));

	logger.info("QPs: " + qualifiedUserParameters);

	templateApplier.apply(qualifiedUserParameters, doc);
	return doc;
    }

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
