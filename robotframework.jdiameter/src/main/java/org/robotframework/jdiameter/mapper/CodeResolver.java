package org.robotframework.jdiameter.mapper;

/**
 * Abstract helper class enabling loading properties from file, parsing and
 * receiving integer properties
 */
public abstract class CodeResolver extends PropertiesReader {

    boolean initialized;
    String fileName;

    public CodeResolver(String name) {
	super();	
	this.fileName = name;
    }

    /**
     * gets an int value of property defined by given name
     * 
     * @param name
     *            name of property
     * @return int value of property
     */
    public int getCode(String name) {
	initialize();
	return Integer.parseInt(props.getProperty(name).trim());
    }

    void initialize() {
	if (!initialized) {
	    loadPropertiesFile(ClassLoader.getSystemResourceAsStream(fileName));
	    initialized = true;
	}
    }
}
