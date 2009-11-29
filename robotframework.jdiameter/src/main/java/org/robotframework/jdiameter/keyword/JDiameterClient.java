package org.robotframework.jdiameter.keyword;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.robotframework.jdiameter.DiameterMessageEncoder;

/**
 * JDiameter client
 * 
 * @author Eliot
 *
 */
public class JDiameterClient {

    /**
     * Pseudo singleton 
     */
    private static JDiameterClient client = new JDiameterClient();

    DiameterMessageEncoder encoder;
    Session session;
    Stack stack;
    Future<Message> responder;
    XMLConfiguration config;
    Request request;

    public Object openConnection(Object[] arg0) {
	try {
	    stack = new org.jdiameter.client.impl.StackImpl();
	    config = encoder.decodeConfiguration(arg0);
	    SessionFactory factory = stack.init(config);
	    stack.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
	    session = factory.getNewSession();
	    return null;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Object receiveMessage(Object[] arg0) {
	try {
	    Object actual = responder.get();
	    if (actual == null) {
		throw new RuntimeException("No message was received...");
	    }
	    encoder.setLastRequest(request);
	    Object expected = encoder.encodeMessage(arg0);
	    encoder.evaluateMessage(expected, actual);
	    return actual;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Object sendMessage(Object[] arg0) {
	try {
	    encoder.setSession(session);
	    request = (Request) encoder.encodeMessage(arg0);
	    responder = session.send(request);
	    return null;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Object closeConnection(Object[] arg0) {
	try {
	    session.release();
	    stack.stop(10, TimeUnit.SECONDS);
	    return null;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * @param robotCodec
     */
    public void setEncoder(DiameterMessageEncoder robotCodec) {
	this.encoder = robotCodec;
    }

    /**
     * Gets singleton instance
     * @return
     */
    public static JDiameterClient getInstance() {
	return client;
    }

}
