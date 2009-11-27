package org.robotframework.jdiameter;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDiameterClient {

    private static Logger logger = LoggerFactory
	    .getLogger(JDiameterClient.class);
    private static JDiameterClient client = new JDiameterClient();

    long timeout;
    DiameterRobotCodec encoder;
    Session connection;
    Stack stack;
    private Future<Message> responder;
    private XMLConfiguration config;
    private Request request;

    public Object openConnection(Object[] arg0) {
	try {
	    long time = System.currentTimeMillis();
	    this.timeout = encoder.decodeTimeout(arg0);
	    stack = new org.jdiameter.client.impl.StackImpl();
	    config = new XMLConfiguration((String) arg0[1]);
	    SessionFactory factory = stack.init(config);
	    stack.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
	    connection = factory.getNewSession();
	    long timeb = System.currentTimeMillis() - time;
	    logger.info("Time handling the open: " + timeb);
	    return timeb;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object receiveMessage(Object[] arg0) {
	try {
	    long time = System.currentTimeMillis();
	    Object actual = responder.get();
	    if (actual == null) {
		logger.info("Time handling the receive: "
			+ (System.currentTimeMillis() - time));
		throw new RuntimeException("No message was received...");
	    }
	    logger.info("Input from user: " + Arrays.toString(arg0));
	    encoder.request = request;
	    Object expected = encoder.encodeMessage(arg0);
	    logger.info("Decoded expected: " + expected);
	    logger.info("Received: " + actual);
	    encoder.evaluateMessage(expected, actual);
	    logger.info("Time handling the receive: "
		    + (System.currentTimeMillis() - time));
	    return actual;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object sendMessage(Object[] arg0) {
	try {
	    long time = System.currentTimeMillis();
	    encoder.session = connection;
	    request = (Request) encoder.encodeMessage(arg0);
	    responder = connection.send(request);
	    long timeb = System.currentTimeMillis() - time;
	    logger.info("Time handling the sendMessage: " + timeb);
	    return timeb;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object closeConnection(Object[] arg0) {
	try {
	    long time = System.currentTimeMillis();
	    connection.release();
	    stack.stop(10, TimeUnit.SECONDS);
	    long timeb = System.currentTimeMillis() - time;
	    logger.info("Time handling the close: " + timeb);
	    return timeb;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public void setEncoder(DiameterRobotCodec robotCodec) {
	logger.info("robot codec: " + robotCodec);
	this.encoder = robotCodec;
    }

    public static JDiameterClient getInstance() {
	return client;
    }

}
