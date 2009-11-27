package org.robotframework.jdiameter;

public abstract class CodeResolver extends PropertiesReader {

    public CodeResolver(String name) {
	super();
	loadPropertiesFile(ClassLoader.getSystemResourceAsStream(name));
    }

    public int getCode(String name) {
	return Integer.parseInt(props.getProperty(name).trim());
    }
}
