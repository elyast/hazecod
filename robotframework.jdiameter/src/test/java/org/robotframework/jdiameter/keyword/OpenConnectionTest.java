package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class OpenConnectionTest {

    private static final String CONFIGURATION = "templateFilePath";
    private static final long TIMEOUT = 0L;

    private OpenConnection testObj;

    @Before
    public void setUp() throws Exception {
	testObj = new OpenConnection();
    }

    @Mocked
    JDiameterClient client;

    @Test
    public void testExecute_correct() {
	Object[] arguments = new Object[] { CONFIGURATION, TIMEOUT };

	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.openConnection(CONFIGURATION, TIMEOUT);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noTimeout() {
	Object[] arguments = new Object[] { CONFIGURATION };

	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.openConnection(CONFIGURATION,
			OpenConnection.DEFAULT_TIMEOUT);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noConfiguration() {
	Object[] arguments = new Object[0];

	new Expectations() {
	    {
		JDiameterClient.getInstance();
		returns(client);
		client.openConnection(OpenConnection.DEFAULT_CONFIGURATION,
			OpenConnection.DEFAULT_TIMEOUT);
	    }
	};

	testObj.execute(arguments);
    }
    
    @Test
    public void testGetArguments() {
	String[] expectedArgNames = new String[] {
		OpenConnection.Argument.CONFIGURATION.name(),
		OpenConnection.Argument.TIMEOUT.name() };

	String[] actualArgNames = testObj.getArgumentNames();

	Assert.assertArrayEquals(expectedArgNames, actualArgNames);
    }
}
