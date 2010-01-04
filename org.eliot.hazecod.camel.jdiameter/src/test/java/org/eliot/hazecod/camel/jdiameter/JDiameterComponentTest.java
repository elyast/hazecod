package org.eliot.hazecod.camel.jdiameter;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
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
public class JDiameterComponentTest {

    private static final int AID = 4;
    private static final int VID = 193;
    private JDiameterComponent testObj;
    @Mocked
    CamelContext context;
    @Mocked
    Exchange mockedExchange;
    @Mocked
    Message inMessage;
    @Mocked 
    Message outMessage;

    class MockProcesor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
	    Request req = (Request)exchange.getIn().getBody();
	    Answer ans = req.createAnswer(ResultCode.SUCCESS);
	    if (ExchangeHelper.isOutCapable(exchange)) {
		exchange.getOut().setBody(ans);
	    } else {
		exchange.getIn().setBody(ans);
	    }
	}
	
    }
    
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
	testObj.setServerConfigurationPath(new File("notNull"));
	Endpoint endpoint = testObj.createEndpoint("tcp://localhost:3868");
	assertNotNull(endpoint);
    }

    @Test(expected = Exception.class)
    public void testCreateEndpointStringStringMap_Exception() throws Exception {
	testObj = new JDiameterComponent();
	testObj.createEndpoint("tcp://localhost:3868");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIntegration() throws Exception {
	final JDiameterEndpoint endpoint = testObj.createEndpoint();
	endpoint.setEndpointUriIfNotSpecified("aaa");
	new Expectations() {
	    {
		mockedExchange.getIn();
		returns(inMessage);
		inMessage.getBody();
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Configuration config = new XMLConfiguration(cl.getResourceAsStream("configuration.xml"));
		Session df = new StackImpl().init(config).getNewSession();
		Request msg = df.createRequest(org.jdiameter.api.Message.ACCOUNTING_REQUEST, 
			ApplicationId.createByAuthAppId(VID, AID), "eliot.cca.org");
		returns(msg);
		mockedExchange.getPattern();
		returns(ExchangePattern.InOut);
		mockedExchange.getOut();
		returns(outMessage);
		mockedExchange.getIn();
		returns(inMessage);
		inMessage.getHeaders();
		returns(null);
		outMessage.setHeaders((Map<String, Object>)withNull());
		mockedExchange.getOut();
		returns(outMessage);
		outMessage.setBody(with(new BaseMatcher<Answer>() {

		    @Override
		    public boolean matches(Object arg0) {
			Answer x = (Answer) arg0;
			try {
			    return x.getAvps().getAvp(Avp.RESULT_CODE).getInteger32() == ResultCode.SUCCESS;
			} catch (AvpDataException e) {
			    throw new RuntimeException(e);
			}
		    }

		    @Override
		    public void describeTo(Description arg0) {
			// TODO Auto-generated method stub
			
		    }
		}));
	    }
	};
	JDiameterConsumer consumer = (JDiameterConsumer) endpoint
		.createConsumer(new MockProcesor());
	consumer.doStart();
	JDiameterProducer producer = (JDiameterProducer) endpoint
		.createProducer();
	producer.doStart();
	producer.process(mockedExchange);
	
	producer.doStop();
	consumer.doStop();
    }

}
