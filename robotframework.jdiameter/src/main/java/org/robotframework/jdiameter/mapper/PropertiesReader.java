package org.robotframework.jdiameter.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads properties file
 * @author Eliot
 *
 */
public abstract class PropertiesReader {

    Properties props;

    public PropertiesReader() {
	props = new Properties();
    }

    /**
     * Loads properties file from stream
     * @param input
     */
    public void loadPropertiesFile(InputStream input) {
	try {
	    props.load(input);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
