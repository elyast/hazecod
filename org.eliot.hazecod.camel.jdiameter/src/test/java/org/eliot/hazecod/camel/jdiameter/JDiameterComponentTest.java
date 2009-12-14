package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.*;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class JDiameterComponentTest {

    private JDiameterComponent testObj;
    @Mocked CamelContext context;
    
    @Before
    public void setUp() throws Exception {
	testObj = new JDiameterComponent(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateEndpointStringStringMap() throws Exception {
	Endpoint endpoint = testObj.createEndpoint("tcp://localhost:3868");
	assertNotNull(endpoint);
    }
    
    @Test
    public void testCreateEndpointStringStringMap_NotNull() throws Exception {
	testObj.setConfigurationPath("notNull");
	Endpoint endpoint = testObj.createEndpoint("tcp://localhost:3868");
	assertNotNull(endpoint);
    }
    
    @Test(expected = Exception.class)
    public void testCreateEndpointStringStringMap_Exception() throws Exception {
	testObj = new JDiameterComponent();
	testObj.createEndpoint("tcp://localhost:3868");
    }

}
