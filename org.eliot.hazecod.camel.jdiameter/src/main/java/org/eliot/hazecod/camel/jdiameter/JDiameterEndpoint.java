package org.eliot.hazecod.camel.jdiameter;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.util.ObjectHelper;

public class JDiameterEndpoint extends DefaultEndpoint {

    JDiameterConfiguration configuration;
    
    public Consumer createConsumer(Processor processor) throws Exception {
	ObjectHelper.notNull(configuration, "configuration"); 
	return new JDiameterConsumer(this, processor, configuration);
    }

    public Producer createProducer() throws Exception {
	ObjectHelper.notNull(configuration, "configuration"); 
	return new JDiameterProducer(this, configuration);
    }

    public boolean isSingleton() {
	return true;
    }

    public void setConfiguration(JDiameterConfiguration configuration) {
	this.configuration = configuration;
    }
    
    public JDiameterConfiguration getConfiguration() {
	return configuration;
    }
    
    
}
