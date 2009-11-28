package org.robotframework.jdiameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesReader {

    Properties props;

    public PropertiesReader() {
	props = new Properties();
    }

    public void loadPropertiesFile(InputStream input) {
	try {
	    props.load(input);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
