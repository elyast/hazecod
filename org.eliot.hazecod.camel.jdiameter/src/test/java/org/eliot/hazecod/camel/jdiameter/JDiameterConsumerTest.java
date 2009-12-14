package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.*;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.jdiameter.api.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class JDiameterConsumerTest {

    private JDiameterConsumer testObj;
    @Mocked Endpoint endpoint;
    @Mocked Processor processor;
    @Mocked Configuration configuration;
    
    @Before
    public void setUp() throws Exception {
	testObj = new JDiameterConsumer(endpoint, processor, configuration);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDoStart() throws Exception {
	new Expectations() {
	    {
		
	    }
	};
	testObj.doStart();
    }

    @Test
    public void testDoStop() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetFromConfiguration() {
	fail("Not yet implemented");
    }

}
