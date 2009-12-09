package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.protocol.Client;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class OpenConnectionTest {

    private static final String CONFIGURATION = "templateFilePath";
    private static final long TIMEOUT = 0L;

    private OpenConnection testObj;

    @Mocked
    Client client;

    @Before
    public void setUp() throws Exception {
	testObj = new OpenConnection();
	testObj.setClient(client);
    }

    @Test
    public void testExecute_correct() {
	Object[] arguments = new Object[] {CONFIGURATION, TIMEOUT };

	new Expectations() {
	    {
		client.openConnection(CONFIGURATION);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noTimeout() {
	Object[] arguments = new Object[] { CONFIGURATION };

	new Expectations() {
	    {
		client.openConnection(CONFIGURATION);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testExecute_noConfiguration() {
	Object[] arguments = new Object[0];

	new Expectations() {
	    {
		client.openConnection(OpenConnection.DEFAULT_CONFIGURATION);
	    }
	};

	testObj.execute(arguments);
    }

    @Test
    public void testGetArguments() {
	String[] expectedArgNames = OpenConnection.ARGUMENT_NAMES;
	String[] actualArgNames = testObj.getArgumentNames();

	Assert.assertArrayEquals(expectedArgNames, actualArgNames);
    }
}
