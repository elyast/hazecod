package org.eliot.hazecod.camel.jdiameter;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.jdiameter.api.Configuration;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

/**
 * @author Eliot
 *
 */
public class JDiameterEndpoint extends DefaultEndpoint {

    static final String SERVER_XML = "server-configuration.xml";
    static final String CLIENT_XML = "configuration.xml";
    String configurationPath;
    
    /**
     * @param processor Processor
     * @return JDiameter server
     * @throws Exception Exception from JDiameter
     */
    public Consumer createConsumer(Processor processor) throws Exception {
	Configuration configuration = new XMLConfiguration(
	    createConfiguration(configurationPath, SERVER_XML));
	return new JDiameterConsumer(this, processor, configuration);
    }

    InputStream createConfiguration(String path, 
	    String defaultPath) throws Exception {
	if (path == null) {
	    ClassLoader cl = getClass().getClassLoader();
	    InputStream istream = cl.getResourceAsStream(defaultPath);
	    return istream;
	} 
	return new FileInputStream(path);
    }

    /**
     * @return Producer (JDiameter Client)
     * @throws Exception Exception from JDiameter
     */
    public Producer createProducer() throws Exception {
	Configuration configuration = 
	    new org.jdiameter.client.impl.helpers.XMLConfiguration(
		createConfiguration(configurationPath, CLIENT_XML));
	return new JDiameterProducer(this, configuration);
    }

    /**
     * @return true
     */
    public boolean isSingleton() {
	return true;
    }

    /**
     * @param configuration Configuration
     */
    public void setConfigurationPath(String configuration) {
	this.configurationPath = configuration;
    }
    
    
}
