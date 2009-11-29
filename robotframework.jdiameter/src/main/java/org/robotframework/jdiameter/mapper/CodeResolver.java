package org.robotframework.jdiameter.mapper;


/**
 * Abstract code resolver from properties files
 * @author wro02896
 *
 */
public abstract class CodeResolver extends PropertiesReader {

    public CodeResolver(String name) {
	super();
	loadPropertiesFile(ClassLoader.getSystemResourceAsStream(name));
    }

    /**
     * Gets code as int
     * @param name
     * @return
     */
    public int getCode(String name) {
	return Integer.parseInt(props.getProperty(name).trim());
    }
}
