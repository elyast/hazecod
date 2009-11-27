package org.robotframework.jdiameter;

/**
 * Abstract helper class enabling loading properties from file,
 * parsing and receiving integer properties  
 */
public abstract class CodeResolver extends PropertiesReader {

    public CodeResolver(String name) {
	super();
	loadPropertiesFile(ClassLoader.getSystemResourceAsStream(name));
    }

    /**
     * gets an int value of property defined by given name
     * @param name name of property
     * @return int value of property
     */
    public int getCode(String name) {
	return Integer.parseInt(props.getProperty(name).trim());
    }
}
