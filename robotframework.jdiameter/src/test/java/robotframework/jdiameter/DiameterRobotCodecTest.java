package robotframework.jdiameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mockit.integration.junit4.JMockit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@RunWith(JMockit.class)
public class DiameterRobotCodecTest {

	private DiameterRobotCodec testObj;
	private ApplicationContext context;
	private Session session;
	
	@Before
	public void setup() throws Exception {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("com/nsn/hydra/robotframework/diameter/keywords.xml");
		}
		testObj = (DiameterRobotCodec)context.getBean("robotCodec");
		session = new StackImpl().init(EmptyConfiguration.getInstance()).getNewSession();
	}
	
	@Test
	public void testDecodeTimeout() throws Exception {
		assertEquals(300, testObj.decodeTimeout(new Object[] {}));
		assertEquals(300, testObj.decodeTimeout(new Object[] {"wro"}));
		assertEquals(300, testObj.decodeTimeout(new Object[] {"bln", "1230"}));
		assertEquals(500, testObj.decodeTimeout(new Object[] {"xyz", "1230", "500"}));
	}

	@Test
	public void testEncodeMessage_MMS_IEC_CCR() throws Exception {
		//TODO refactor this tests to be more descriptive!!!
		Message msg = (Message)testObj.encodeMessage(new Object[] {"MMS-IEC-CCR","SessId=lukasz;12345",
				"From=486018020793","From-Location=2602","To_0=48602801829","To_0-Location=2602", "CurrentTime=15632323",
				"MMS-Size=150",	"MMS-SendTime=15632323"});
		assertEquals(272, msg.getCommandCode());
		assertEquals(4, msg.getApplicationId());
		assertTrue(msg.isRequest());
		AvpSet avps = msg.getAvps();
		assertEquals(14, avps.size());
		int[] array = {55, 258, 263, 264, 283, 293, 296, 415, 416, 436, 439, 443, 461, 873};
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], avps.getAvpByIndex(i).getCode());
		}
		assertEquals("lukasz;12345", avps.getAvpByIndex(2).getUTF8String());
		assertEquals("486018020793", avps.getAvpByIndex(11).getGrouped().getAvpByIndex(1).getUTF8String());
		Avp serviceInfo = avps.getAvpByIndex(13);
		assertEquals(10415, serviceInfo.getVendorId());
		Avp ps = serviceInfo.getGrouped().getAvpByIndex(0);
		assertEquals(10415, ps.getVendorId());
		assertEquals(874, ps.getCode());
		Avp mms = serviceInfo.getGrouped().getAvpByIndex(1);
		assertEquals(877, mms.getCode());
		assertEquals(10415, mms.getVendorId());
		
		assertEquals("486018020793", mms.getGrouped().getAvpByIndex(0).getGrouped().getAvpByIndex(2).getUTF8String());
	}
	
	
	@Test
	public void testEncodeMessage_MMS_IEC_CCA() throws Exception {
		Message msg = (Message)testObj.encodeMessage(new Object[] {
				"MMS-IEC-CCA","SessId=lukasz;12345","Result=SUCCESS"});
		assertEquals(272, msg.getCommandCode());
		assertEquals(4, msg.getApplicationId());
		assertFalse(msg.isRequest());
		AvpSet avps = msg.getAvps();
		assertEquals(4, avps.size());
		int[] array = {263, 268, 415, 416};
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], avps.getAvpByIndex(i).getCode());
		}
		assertEquals("lukasz;12345", avps.getAvpByIndex(0).getUTF8String());
		assertEquals(2001, avps.getAvpByIndex(1).getInteger32());
	}
	
	@Test
	public void testEvaluateMessage_NoException() throws Exception {
		Message msg = (Message)testObj.encodeMessage(new Object[] {
				"MMS-IEC-CCA","SessId=lukasz;12345","Result=SUCCESS"});
		
		Request exp = session.createRequest(272, ApplicationId.createByAccAppId(4), "eliot.org");
		
		testObj.evaluateMessage(exp.createAnswer(2001), msg);
		//expected no exception
	}
	
	@Test
	public void testEvaluateMessage_NoException_DeepSubtree() throws Exception {
		Message msg = (Message)testObj.encodeMessage(new Object[] {"MMS-IEC-CCR","SessId=lukasz;12345",
				"From=486018020793","From-Location=2602","To_0=48602801829","To_0-Location=2602", "CurrentTime=2009-08-06 11:31:31",
				"MMS-Size=150",	"MMS-SendTime=2009-08-06 11:31:31"});
		Request exp = session.createRequest(272, ApplicationId.createByAccAppId(4), "eliot.org");
		AvpSet f = exp.getAvps();
		AvpSet data = f.addGroupedAvp(873, 10415, true, true);
		AvpSet mmsdata = data.addGroupedAvp(877, 10415, true, true);
		AvpSet to1 = mmsdata.addGroupedAvp(1201, 10415, true, true);
		Avp address = f.addAvp(897, "48602801829", 10415, true, false, true);
		to1.addAvp(address);
		testObj.evaluateMessage(exp, msg);
		//expected no exception
	}
	
	@Test
	public void testEvaluateMessage_Exception() throws Exception {
		Message msg = (Message)testObj.encodeMessage(new Object[] {
				"MMS-IEC-CCA","SessId=lukasz;12345","Result=SUCCESS"});
		Message exp = session.createRequest(272, ApplicationId.createByAccAppId(4), "eliot.org");
		exp = ((Request)exp).createAnswer(2002);
		try {
			testObj.evaluateMessage(exp, msg);
			fail();
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("2002") && e.getMessage().contains("2001"));
		}
	}
}
