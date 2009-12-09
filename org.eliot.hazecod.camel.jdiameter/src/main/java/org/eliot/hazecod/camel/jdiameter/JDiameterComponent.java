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

    
    JDiameterConfiguration configuration;
    
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
    protected Endpoint createEndpoint(String uri, String remaining,
	    Map parameters) throws Exception {
	JDiameterEndpoint endpoint = new JDiameterEndpoint();
	endpoint.setConfiguration(configuration);
	setProperties(configuration, parameters);
	endpoint.setExchangePattern(ExchangePattern.InOut);
	return endpoint;
    }

    /**
     * @param configuration Configuration 
     */
    public void setConfiguration(JDiameterConfiguration configuration) {
	this.configuration = configuration;
    }
    
    /**
     * @return Configuration
     */
    public JDiameterConfiguration getConfiguration() {
	return configuration;
    }
}
