package org.robotframework.jdiameter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import junit.framework.AssertionFailedError;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JMockit.class)
public class DiameterMessageComparatorTest {

    private DiameterMessageComparator testObj;
    private ApplicationContext context;
    private Session session;

    @Before
    public void setup() throws Exception {
	if (context == null) {
	    context = new ClassPathXmlApplicationContext(
		    "robotframework/jdiameter/keywords.xml");
	}
	testObj = (DiameterMessageComparator) context.getBean("messageComparator");
	StackImpl stackImpl = new StackImpl();
	InputStream istream = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("configuration.xml");
	SessionFactory init = stackImpl.init(new XMLConfiguration(istream));
	session = init.getNewSession();
    }


    @Test
    public void testEvaluateMessage_NoException() throws Exception {
	Request exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	testObj.evaluateMessage(exp.createAnswer(2001), exp.createAnswer(2001));
	// expected no exception
    }

    @Test
    public void testEvaluateMessage_NoException_DeepSubtree() throws Exception {
	Request exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	Request msg = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	
	AvpSet rootAvp = exp.getAvps();
	
	removeAll(rootAvp);
	AvpSet data = rootAvp.addGroupedAvp(873, 10415, true, true);
	AvpSet mmsdata = data.addGroupedAvp(877, 10415, true, true);
	AvpSet to1 = mmsdata.addGroupedAvp(1201, 10415, true, true);
	to1.addAvp(897, "48602801829", 10415, true, false, true);
	
	AvpSet data2 = msg.getAvps().addGroupedAvp(873, 10415, true, true);
	AvpSet mmsdata2 = data2.addGroupedAvp(877, 10415, true, true);
	AvpSet to2 = mmsdata2.addGroupedAvp(1201, 10415, true, true);
	to2.addAvp(897, "48602801829", 10415, true, false, true);
	
	testObj.evaluateMessage(exp, msg);
	// expected no exception
    }

    private void removeAll(AvpSet rootAvp) {
	for(int i=0; i < rootAvp.size(); ) {
	    rootAvp.removeAvpByIndex(0);
	}
    }


    @Test
    public void testEvaluateMessage_Exception() throws Exception {
	Request exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	try {
	    testObj.evaluateMessage(exp.createAnswer(2002), exp.createAnswer(2001));
	    fail();
	} catch (AssertionFailedError e) {
	    assertTrue(e.getMessage().contains("2002")
		    && e.getMessage().contains("2001"));
	}
    }
    
    @Mocked Message actual;
    @Mocked Message expected;
    
    @Test(expected=AssertionFailedError.class)
    public void testEvaluateMessage_NullActualAvp() throws Exception {
	new Expectations(){
	    
	    @Mocked AvpSet actualAvps;
	    @Mocked AvpSet expectedAvps;
	    
	    @Mocked Avp expectedAvp;
	    @Mocked Iterator<Avp> expectedAvpsIterator;
	    
	    private final int AVP_CODE = new Random().nextInt();
	    
	    {
		expected.getApplicationId();
		actual.getApplicationId();
		expected.getCommandCode();
		actual.getCommandCode();
		expected.isError();
		actual.isError();
		expected.isProxiable();
		actual.isProxiable();
		expected.isRequest();
		actual.isRequest();
		expected.isReTransmitted();
		actual.isReTransmitted();
		
		actual.getAvps();
		returns(actualAvps);
		expected.getAvps();
		returns(expectedAvps);
		
		expectedAvps.iterator();
		returns(expectedAvpsIterator);
		expectedAvpsIterator.hasNext();
		returns(true);
		expectedAvpsIterator.next();
		returns(expectedAvp);
		
		expectedAvp.getCode();
		returns(AVP_CODE);
		actualAvps.getAvp(AVP_CODE);
		returns(null);
	    }
	};
	
	testObj.evaluateMessage(expected, actual);
    }
    
    @Test(expected=AssertionFailedError.class)
    public void testEvaluateMessage_DifferentApplicationId() throws Exception {
	new Expectations(){
	    final long APPLICATION_ID = 0;
	    final long DIFFERENT_APPLICATION_ID = APPLICATION_ID + 1;
	    {
		expected.getApplicationId();
		returns(APPLICATION_ID);
		actual.getApplicationId();
		returns(DIFFERENT_APPLICATION_ID);
	    }
	};
	
	testObj.evaluateMessage(expected, actual);
    }
}
