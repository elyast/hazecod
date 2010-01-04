/**
 * 
 */
package org.eliot.hazecod.camel.jdiameter;

import java.io.File;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultComponent;

/**
 * @author Eliot.org
 *
 */
public class JDiameterComponent extends DefaultComponent {

    
    File serverConfigurationPath;
    File clientConfigurationPath;
    
    /**
     * 
     */
    public JDiameterComponent() {
    }
    
    /**
     * @param context Camel Context
     */
    public JDiameterComponent(CamelContext context) {
	super(context);
    }


    /**
     *  
     * @see
     * org.apache.camel.impl.DefaultComponent#createEndpoint(java.lang.String,
     * java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    protected Endpoint createEndpoint(String uri, String remaining,
	    Map parameters) throws Exception {
	JDiameterEndpoint endpoint = createEndpoint();
	if (serverConfigurationPath != null) {
	    setProperties(serverConfigurationPath, parameters);
	}
	if (clientConfigurationPath != null) {
	    setProperties(clientConfigurationPath, parameters);
	}	
	return endpoint;
    }
    
    /**
     * @return JDiameterEndpoint
     */
    public JDiameterEndpoint createEndpoint() {
	JDiameterEndpoint endpoint = new JDiameterEndpoint();
	String serverPath = null;
	if (serverConfigurationPath != null) {
	    serverPath = serverConfigurationPath.getAbsolutePath();
	}
	endpoint.setServerConfigurationPath(serverPath);
	String clientPath = null;
	if (clientConfigurationPath != null) {
	    clientPath = clientConfigurationPath.getAbsolutePath();
	}
	endpoint.setClientConfigurationPath(clientPath);
	endpoint.setExchangePattern(ExchangePattern.InOut);
	return endpoint;
    }

    /**
     * @param configurationPath Path to configuration
     */
    public void setServerConfigurationPath(File configurationPath) {
	this.serverConfigurationPath = configurationPath;
    }
    
    /**
     * @return Configuration path
     */
    public File getServerConfigurationPath() {
	return serverConfigurationPath;
    }

    /**
     * @param clientConfigurationPath Client configuration path
     */
    public void setClientConfigurationPath(File clientConfigurationPath) {
	this.clientConfigurationPath = clientConfigurationPath;
    }
    
    /**
     * @return Configuration path of client
     */
    public File getClientConfigurationPath() {
	return clientConfigurationPath;
    }
}
