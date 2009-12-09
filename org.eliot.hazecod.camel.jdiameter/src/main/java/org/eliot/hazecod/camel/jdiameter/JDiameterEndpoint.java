package org.eliot.hazecod.camel.jdiameter;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.util.ObjectHelper;

/**
 * @author Eliot
 *
 */
public class JDiameterEndpoint extends DefaultEndpoint {

    JDiameterConfiguration configuration;
    
    /**
     * @param processor Processor
     * @return JDiameter server
     * @throws Exception Exception from JDiameter
     */
    public Consumer createConsumer(Processor processor) throws Exception {
	ObjectHelper.notNull(configuration, "configuration"); 
	return new JDiameterConsumer(this, processor, configuration);
    }

    /**
     * @return Producer (JDiameter Client)
     * @throws Exception Exception from JDiameter
     */
    public Producer createProducer() throws Exception {
	ObjectHelper.notNull(configuration, "configuration"); 
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
