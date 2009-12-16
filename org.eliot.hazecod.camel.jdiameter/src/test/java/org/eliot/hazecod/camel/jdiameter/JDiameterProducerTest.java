package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.*;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.Endpoint;
import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.StackState;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class JDiameterProducerTest {
    
    private JDiameterProducer testObj;
    @Mocked Endpoint endpoint;

    @Before
    public void setUp() throws Exception {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	Configuration config = new XMLConfiguration(
		cl.getResourceAsStream(JDiameterEndpoint.CLIENT_XML));
	testObj = new JDiameterProducer(endpoint, config);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDoStart() throws Exception {
	testObj.doStart();
	StackImpl stack = (StackImpl)testObj.stack;
	assertEquals(StackState.STARTED, stack.getState());
    }

    @Test
    public void testDoStop() throws Exception {
	testObj.doStart();
	testObj.doStop();
	StackImpl stack = (StackImpl)testObj.stack;
	assertEquals(StackState.IDLE, stack.getState());
    }


}
