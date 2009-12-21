package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class JDiameterEndpointTest {

    private JDiameterEndpoint testObj;
    
    @Mocked Processor processor;
    
    @Before
    public void setUp() throws Exception {
	testObj = new JDiameterEndpoint();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateConsumer() throws Exception {
	Consumer consumer = testObj.createConsumer(processor);
	assertNotNull(consumer);
    }

    @Test
    public void testCreateConfiguration_Server() throws Exception {
	InputStream is = testObj.createConfiguration(null, JDiameterEndpoint.SERVER_XML);
	assertNotNull(is);
    }
    
    @Test
    public void testCreateConfiguration_Client() throws Exception {
	InputStream is = testObj.createConfiguration(null, JDiameterEndpoint.CLIENT_XML);
	assertNotNull(is);
    }
    
    @Test
    public void testCreateConfiguration_Custom() throws Exception {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	URL url = cl.getResource(JDiameterEndpoint.SERVER_XML);
	InputStream is = testObj.createConfiguration(
		new File(url.toURI()).getAbsolutePath(), JDiameterEndpoint.SERVER_XML);
	assertNotNull(is);
    }
    
    @Test
    public void testCreateProducer() throws Exception {
	Producer producer = testObj.createProducer();
	assertNotNull(producer);
    }

}
