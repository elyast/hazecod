package org.robotframework.jdiameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

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
public class DiameterMessageEncoderTest {

    private DiameterMessageEncoder testObj;
    private ApplicationContext context;
    private Session session;

    @Before
    public void setup() throws Exception {
	if (context == null) {
	    context = new ClassPathXmlApplicationContext(
		    "robotframework/jdiameter/keywords.xml");
	}
	testObj = (DiameterMessageEncoder) context.getBean("robotCodec");
	StackImpl stackImpl = new StackImpl();
	InputStream istream = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("configuration.xml");
	SessionFactory init = stackImpl.init(new XMLConfiguration(istream));
	session = init.getNewSession();
	testObj.session = session;
	testObj.lastRequest = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");
    }

    @Test
    public void testEncodeMessage_MMS_IEC_CCR() throws Exception {
	// TODO refactor this tests to be more descriptive!!!
	Message msg = (Message) testObj.encodeMessage("MMS-IEC-CCR",
		new String[] { "SessId=lukasz;12345", "From=486018020793",
			"From-Location=2602", "To_0=48602801829",
			"To_0-Location=2602",
			"CurrentTime=2009-08-07 00:00:00", "MMS-Size=150",
			"MMS-SendTime=2009-08-07 00:00:00" });
	assertEquals(272, msg.getCommandCode());
	assertEquals(4, msg.getApplicationId());
	assertTrue(msg.isRequest());
	AvpSet avps = msg.getAvps();
	for (Avp avp : avps) {
	    System.out.println(avp.getCode());
	}
	// assertEquals(14, avps.size());
	// 258,416,
	int[] array = { 258, 416, 55, 263, 264, 283, 293, 296, 415, 436, 439,
		443, 461, 873 };
	// 259
	for (int i = 0; i < array.length; i++) {
	    Avp avp = avps.getAvp(array[i]);
	    assertNotNull(avp);
	}
	assertEquals("lukasz;12345", avps.getAvp(263).getUTF8String());
	AvpSet grouped = avps.getAvp(443).getGrouped();
	for (Avp avp : grouped) {
	    System.out.println(avp.getCode());
	}
	assertEquals("486018020793", grouped.getAvp(444).getUTF8String());
	Avp serviceInfo = avps.getAvp(873);
	assertEquals(10415, serviceInfo.getVendorId());
	Avp ps = serviceInfo.getGrouped().getAvpByIndex(0);
	assertEquals(10415, ps.getVendorId());
	assertEquals(874, ps.getCode());
	Avp mms = serviceInfo.getGrouped().getAvpByIndex(1);
	assertEquals(877, mms.getCode());
	assertEquals(10415, mms.getVendorId());

	assertEquals("486018020793", mms.getGrouped().getAvpByIndex(0)
		.getGrouped().getAvpByIndex(2).getUTF8String());
    }

    @Test
    public void testEncodeMessage_MMS_IEC_CCA() throws Exception {
	Message msg = (Message) testObj.encodeMessage("MMS-IEC-CCA",
		new String[] { "SessId=lukasz;12345", "Result=SUCCESS" });
	assertEquals(272, msg.getCommandCode());
	assertEquals(4, msg.getApplicationId());
	assertFalse(msg.isRequest());
	AvpSet avps = msg.getAvps();
	// assertEquals(4, avps.size());
	int[] array = { 263, 268, 415, 416 };
	for (int i = 0; i < array.length; i++) {
	    assertNotNull(avps.getAvp(array[i]));
	}
	assertEquals("lukasz;12345", avps.getAvp(263).getUTF8String());
	assertEquals(2001, avps.getAvp(268).getInteger32());
    }

    @Test
    public void testEvaluateMessage_NoException() throws Exception {
	Request exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	Message msg = (Message) testObj
		.encodeMessage("MMS-IEC-CCA", new String[] {
			"SessId=" + exp.getSessionId(), "Result=SUCCESS" });

	testObj.evaluateMessage(exp.createAnswer(2001), msg);
	// expected no exception
    }

    @Test
    public void testEvaluateMessage_NoException_DeepSubtree() throws Exception {
	Request exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	Message msg = (Message) testObj.encodeMessage("MMS-IEC-CCR",
		new String[] { "SessId=" + exp.getSessionId(),
			"From=486018020793", "From-Location=2602",
			"To_0=48602801829", "To_0-Location=2602",
			"CurrentTime=2009-08-06 11:31:31", "MMS-Size=150",
			"MMS-SendTime=2009-08-06 11:31:31" });
	AvpSet rootAvp = exp.getAvps();
	AvpSet data = rootAvp.addGroupedAvp(873, 10415, true, true);
	AvpSet mmsdata = data.addGroupedAvp(877, 10415, true, true);
	AvpSet to1 = mmsdata.addGroupedAvp(1201, 10415, true, true);
	to1.addAvp(897, "48602801829", 10415, true, false, true);
	testObj.evaluateMessage(exp, msg);
	// expected no exception
    }

    @Test
    public void testEvaluateMessage_Exception() throws Exception {
	Message exp = session.createRequest(272, ApplicationId
		.createByAccAppId(4), "eliot.org");

	Message msg = (Message) testObj
		.encodeMessage("MMS-IEC-CCA", new String[] {
			"SessId=" + exp.getSessionId(), "Result=SUCCESS" });
	exp = ((Request) exp).createAnswer(2002);
	try {
	    testObj.evaluateMessage(exp, msg);
	    fail();
	} catch (RuntimeException e) {
	    assertTrue(e.getMessage().contains("2002")
		    && e.getMessage().contains("2001"));
	}
    }
    
    @Mocked Message actual;
    @Mocked Message expected;
    
    @Test(expected=RuntimeException.class)
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
    
    @Test(expected=RuntimeException.class)
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
