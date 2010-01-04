package org.eliot.hazecod.camel.jdiameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.jdiameter.api.Configuration;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eliot
 *
 */
public class JDiameterEndpoint extends DefaultEndpoint {

    static Logger logger = LoggerFactory.getLogger(JDiameterEndpoint.class);
    static final String SERVER_XML = "server-configuration.xml";
    static final String CLIENT_XML = "configuration.xml";
    String serverConfigurationPath;
    String clientConfigurationPath;
    
    /**
     * @param processor Processor
     * @return JDiameter server
     * @throws Exception Exception from JDiameter
     */
    public Consumer createConsumer(Processor processor) throws Exception {
	Configuration configuration = new XMLConfiguration(
	    createConfiguration(serverConfigurationPath, SERVER_XML));
	return new JDiameterConsumer(this, processor, configuration);
    }

    InputStream createConfiguration(String path, 
	    String defaultPath) throws Exception {
	if (path == null) {
	    ClassLoader cl = getClass().getClassLoader();
	    InputStream istream = cl.getResourceAsStream(defaultPath);
	    return istream;
	} 
	File file = new File(path);
	logger.info(file.getAbsolutePath());
	return new FileInputStream(file);
    }

    /**
     * @return Producer (JDiameter Client)
     * @throws Exception Exception from JDiameter
     */
    public Producer createProducer() throws Exception {
	Configuration configuration = 
	    new org.jdiameter.client.impl.helpers.XMLConfiguration(
		createConfiguration(clientConfigurationPath, CLIENT_XML));
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
    public void setServerConfigurationPath(String configuration) {
	this.serverConfigurationPath = configuration;
    }

    /**
     * @param clientPath Client pathConfiguration
     */
    public void setClientConfigurationPath(String clientPath) {
	this.clientConfigurationPath = clientPath;
    }
    
    
}
