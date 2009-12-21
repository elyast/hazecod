/**
 * 
 */
package org.eliot.hazecod.camel.jdiameter;

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

    
    String configurationPath;
    
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
	if (configurationPath != null) {
	    setProperties(configurationPath, parameters);
	}
	return endpoint;
    }
    
    /**
     * @return JDiameterEndpoint
     */
    public JDiameterEndpoint createEndpoint() {
	JDiameterEndpoint endpoint = new JDiameterEndpoint();
	endpoint.setConfigurationPath(configurationPath);
	endpoint.setExchangePattern(ExchangePattern.InOut);
	return endpoint;
    }

    /**
     * @param configurationPath Path to configuration
     */
    public void setConfigurationPath(String configurationPath) {
	this.configurationPath = configurationPath;
    }
    
    /**
     * @return Configuration path
     */
    public String getConfigurationPath() {
	return configurationPath;
    }

}
