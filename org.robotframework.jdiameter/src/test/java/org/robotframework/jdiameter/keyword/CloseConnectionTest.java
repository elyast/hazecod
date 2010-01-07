package org.robotframework.jdiameter.keyword;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robotframework.protocol.Client;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class CloseConnectionTest {

    private CloseConnection testObj;

    @Before
    public void setUp() throws Exception {
	testObj = new CloseConnection();
	testObj.setClient(client);
    }

    @Mocked
    Client client;

    @Test
    public void testExecute() {
	Object[] arguments = null;

	new Expectations() {
	    {
		client.closeConnection();
	    }
	};

	testObj.execute(arguments);
    }

}
