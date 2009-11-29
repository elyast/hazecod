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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDiameter client
 * 
 * @author Eliot
 * 
 */
public class JDiameterClient {

    private static Logger logger = LoggerFactory
	    .getLogger(JDiameterClient.class);
    private static JDiameterClient client = new JDiameterClient();

    // TODO timeout is not used anywhere, can it be removed?
    long timeout;
    DiameterMessageEncoder encoder;
    Session session;
    Stack stack;
    private Future<Message> responder;
    private XMLConfiguration config;
    private Request request;

    public Object openConnection(String configuration, long timeout) {
	try {
	    this.timeout = timeout;
	    config = encoder.decodeConfiguration(configuration);

	    stack = new org.jdiameter.client.impl.StackImpl();
	    SessionFactory factory = stack.init(config);
	    stack.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
	    session = factory.getNewSession();
	    return null;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Object receiveMessage(String template, String[] avps) {
	try {
	    Message actual = responder.get();
	    if (actual == null) {
		throw new RuntimeException("No message was received...");
	    }
	    encoder.setLastRequest(request);
	    Message expected = encoder.encodeMessage(template, avps);
	    encoder.evaluateMessage(expected, actual);
	    return actual;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Object sendMessage(String template, String[] avps) {
	try {
	    encoder.setSession(session);
	    request = (Request) encoder.encodeMessage(template, avps);
	    responder = session.send(request);
	    return null;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object closeConnection() {
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
     * 
     * @return
     */
    public static JDiameterClient getInstance() {
	return client;
    }

}
