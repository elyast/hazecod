package org.robotframework.jdiameter.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Abstract helper class enabling loading properties from InputStream
 */
public abstract class PropertiesReader {

    Properties props;

    /**
     * 
     */
    public PropertiesReader() {
	props = new Properties();
    }

    /**
     * loads properties from given InputStream
     * 
     * @param input
     *            InputStream used to load properties
     */
    public void loadPropertiesFile(InputStream input) {
	try {
	    props.load(input);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
