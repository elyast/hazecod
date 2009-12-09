package org.robotframework.jdiameter;

import static org.junit.Assert.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eliot
 *
 */
@RunWith(JMockit.class)
public class JDiameterClientTest {

    static final int DEF_APP_ID = 4;

    static final int VENDOR_ID = 2;

    static final int CCR_CCA = 272;

    private JDiameterClient testObj;

    @Mocked
    Session mockedSession;
    @Mocked
    Stack mockedStack;
    @Mocked
    Request request;
    @Mocked
    Future<Message> responder;
    @Mocked
    Answer answer;

    @Before
    public void setup() {
	testObj = new JDiameterClient();
    }

    @Test
    public void testOpenConnection_Null() {
	testObj.openConnection(null);
	assertNotNull(testObj.getSession());
	assertNotNull(testObj.stack);
    }

    @Test
    public void testCloseConnection() throws Exception {
	testObj.session = mockedSession;
	testObj.stack = mockedStack;
	new Expectations() {
	    {
		mockedSession.release();
		mockedStack.stop((Long) withAny(0L),
			(TimeUnit) withAny(TimeUnit.MINUTES));
		mockedStack.destroy();
	    }
	};
	testObj.closeConnection();
    }

    @Test
    public void testReceiveMessage() throws Exception {
	testObj.session = mockedSession;
	testObj.responder = responder;
	new Expectations() {
	    {
		responder.get();
		returns(answer);
	    }
	};
	testObj.receiveMessage();
    }

    @Test
    public void testSendMessage() throws Exception {
	testObj.session = mockedSession;
	new Expectations() {
	    {
		mockedSession.send(request);
	    }
	};
	testObj.sendMessage(request);
	assertNotNull(testObj.getLastRequest());
    }

    @Test
    public void testPrettyPrint() throws Exception {
	testObj.openConnection(null);
	Session session = (Session) testObj.getSession();
	Request request2 = session.createRequest(CCR_CCA, org.jdiameter.api.ApplicationId
		.createByAccAppId(VENDOR_ID, DEF_APP_ID), "realm");
	testObj.prettyPrint(request2);
    }
}
