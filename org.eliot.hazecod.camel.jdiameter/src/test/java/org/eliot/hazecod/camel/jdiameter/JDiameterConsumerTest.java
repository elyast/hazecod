package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.assertEquals;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.StackState;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
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

    private static final int ACCT_APP_ID = 19302;
    private static final int AUTH_APP_ID = 4;
    private JDiameterConsumer testObj;
    @Mocked Endpoint endpoint;
    @Mocked Processor processor;
    @Mocked Request request;
    @Mocked Exchange exchange;
    @Mocked Message message;
    @Mocked Answer answer;
    
    @Before
    public void setUp() throws Exception {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	Configuration config = new XMLConfiguration(
		cl.getResourceAsStream(JDiameterEndpoint.SERVER_XML));
	testObj = new JDiameterConsumer(endpoint, processor, config);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDoStart() throws Exception {
	testObj.doStart();
	assertEquals(StackState.STARTED, ((StackImpl)testObj.stack).getState());
	testObj.doStop();
    }

    @Test
    public void testDoStop() throws Exception {
	testObj.doStart();
	testObj.doStop();
	assertEquals(StackState.IDLE, ((StackImpl)testObj.stack).getState());
    }

    @Test
    public void testGetFromConfiguration() {
	ApplicationId[] appIds = testObj.getFromConfiguration(testObj.configuration);
	assertEquals(JDiameterConsumer.VENDOR_ID, appIds[0].getVendorId());
	assertEquals(ACCT_APP_ID, appIds[0].getAcctAppId());
	assertEquals(JDiameterConsumer.VENDOR_ID, appIds[1].getVendorId());
	assertEquals(AUTH_APP_ID, appIds[1].getAuthAppId());
    }
    
    @Test
    public void testProcessRequests() throws Exception {
	new Expectations() {
	    {
		endpoint.createExchange();
		returns(exchange);
		exchange.getIn();
		returns(message);
		message.setBody(request);
		processor.process(exchange);
		exchange.getPattern();
		returns(ExchangePattern.InOnly);
		exchange.getIn();
		returns(message);
		message.getBody();
		returns(answer);
	    }
	};
	testObj.doStart();
	testObj.netListener.processRequest(request);
	testObj.doStop();
    }

}
