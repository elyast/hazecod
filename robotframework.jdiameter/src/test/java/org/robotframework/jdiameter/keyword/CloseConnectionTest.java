package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.jdiameter.JDiameterClient;

@RunWith(JMockit.class)
public class CloseConnectionTest {

    private CloseConnection testObj;

    @Before
    public void setUp() throws Exception {
	testObj = new CloseConnection();
    }

    @Mocked
    JDiameterClient client;

    @Test
    public void testExecute() {
	Object[] arguments = null;

	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.closeConnection();
	    }
	};

	testObj.execute(arguments);
    }

}
