package org.robotframework.jdiameter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrict;
import mockit.integration.junit4.JMockit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.jdiameter.mapper.AvpPrinter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class DiameterMessageComparatorTest {

    static final int SERVICE_INFORMATION = 873;
    static final int ADDRESS_DATA = 897;
    static final int RECIPIENT_ADDRESS = 1201;
    static final int MMS_INFORMATION = 877;
    static final int CCR_CCA = 272;
    public static final int DEFAULT_APP_ID = 4;
    public static final int GPP = 10415;
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
	Request exp = session.createRequest(CCR_CCA, ApplicationId
		.createByAccAppId(DEFAULT_APP_ID), "eliot.org");

	testObj.evaluateMessage(exp.createAnswer(ResultCode.SUCCESS), exp.createAnswer(ResultCode.SUCCESS));
	// expected no exception
    }

    @Test
    public void testEvaluateMessage_NoException_DeepSubtree() throws Exception {
	Request exp = session.createRequest(CCR_CCA, ApplicationId
		.createByAccAppId(DEFAULT_APP_ID), "eliot.org");

	Request msg = session.createRequest(CCR_CCA, ApplicationId
		.createByAccAppId(DEFAULT_APP_ID), "eliot.org");

	
	AvpSet rootAvp = exp.getAvps();
	
	removeAll(rootAvp);
	AvpSet data = rootAvp.addGroupedAvp(SERVICE_INFORMATION, GPP, true, true);
	AvpSet mmsdata = data.addGroupedAvp(MMS_INFORMATION, GPP, true, true);
	AvpSet to1 = mmsdata.addGroupedAvp(RECIPIENT_ADDRESS, GPP, true, true);
	to1.addAvp(ADDRESS_DATA, "48602801829", GPP, true, false, true);
	
	AvpSet data2 = msg.getAvps().addGroupedAvp(SERVICE_INFORMATION, GPP, true, true);
	AvpSet mmsdata2 = data2.addGroupedAvp(MMS_INFORMATION, GPP, true, true);
	AvpSet to2 = mmsdata2.addGroupedAvp(RECIPIENT_ADDRESS, GPP, true, true);
	to2.addAvp(ADDRESS_DATA, "48602801829", GPP, true, false, true);
	
	testObj.evaluateMessage(exp, msg);
	// expected no exception
    }

    private void removeAll(AvpSet rootAvp) {
	for(int i=0; i < rootAvp.size();) {
	    rootAvp.removeAvpByIndex(0);
	}
    }


    @Test
    public void testEvaluateMessage_Exception() throws Exception {
	Request exp = session.createRequest(CCR_CCA, ApplicationId
		.createByAccAppId(DEFAULT_APP_ID), "eliot.org");

	try {
	    testObj.evaluateMessage(exp.createAnswer(ResultCode.LIMITED_SUCCESS), 
		    exp.createAnswer(ResultCode.SUCCESS));
	    fail();
	} catch (RuntimeException e) {
	    assertTrue(e.getMessage().contains("2002")
		    && e.getMessage().contains("2001"));
	}
    }
    
    @Mocked Message actual;
    @Mocked Message expected;
    @NonStrict AvpPrinter printer;
    
    @Test(expected=RuntimeException.class)
    public void testEvaluateMessage_NullActualAvp() throws Exception {
	new Expectations(){
	    
	    @Mocked AvpSet actualAvps;
	    @Mocked AvpSet expectedAvps;
	    
	    @Mocked Avp expectedAvp;
	    @Mocked Iterator<Avp> expectedAvpsIterator;
	    
	    final int avpCode = new Random().nextInt();
	    
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
		returns(avpCode);
		actualAvps.getAvp(avpCode);
		returns(null);
	    }
	};
	testObj.setPrinter(printer);
	testObj.evaluateMessage(expected, actual);
    }
    
    @Test(expected=RuntimeException.class)
    public void testEvaluateMessage_DifferentApplicationId() throws Exception {
	new Expectations(){
	    final long applicationId = 0;
	    final long defaultApplicationId = applicationId + 1;
	    {
		expected.getApplicationId();
		returns(applicationId);
		actual.getApplicationId();
		returns(defaultApplicationId);
	    }
	};
	
	testObj.evaluateMessage(expected, actual);
    }
}
