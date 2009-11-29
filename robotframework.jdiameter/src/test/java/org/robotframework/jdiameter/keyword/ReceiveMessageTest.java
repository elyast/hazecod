package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.jdiameter.JDiameterClient;

@RunWith(JMockit.class)
public class ReceiveMessageTest {

    private static final String AVP_AND_VALUE1 = "avp1=value1";
    private static final String AVP_AND_VALUE2 = "avp2=value2";
    private static final String TEMPLATE_FILE_PATH = "templateFilePath";
    private ReceiveMessage testObj;

    @Before
    public void setUp() throws Exception {
	testObj = new ReceiveMessage();
    }

    @Mocked
    JDiameterClient client;

    @Test
    public void testExecute_correct() {
	Object[] arguments = new Object[] { TEMPLATE_FILE_PATH, AVP_AND_VALUE1,
		AVP_AND_VALUE2 };
	final String[] expectedAVPs = new String[] { AVP_AND_VALUE1,
		AVP_AND_VALUE2 };

	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.receiveMessage(TEMPLATE_FILE_PATH, withEqual(expectedAVPs));
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noAvps() {
	Object[] arguments = new Object[] { TEMPLATE_FILE_PATH };
	final String[] avps = new String[0];
	
	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.receiveMessage(TEMPLATE_FILE_PATH, withEqual(avps));
	    }
	};

	testObj.execute(arguments);
    }
    
    @Test(expected=RuntimeException.class)
    public void testExecute_noTemplate() {
	Object[] arguments = new Object[0];
	
	testObj.execute(arguments);
    }
}
