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

    // TODO timeout is not used anywhere, can it be removed?
    long timeout;
    DiameterRobotCodec encoder;
    Session connection;
    Stack stack;
    private Future<Message> responder;
    private XMLConfiguration config;
    private Request request;

    public long openConnection(String configuration, long timeout) {
	try {
	    long startTime = System.currentTimeMillis();

	    this.timeout = timeout;
	    config = encoder.decodeConfiguration(configuration);

	    stack = new org.jdiameter.client.impl.StackImpl();
	    SessionFactory factory = stack.init(config);
	    stack.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
	    connection = factory.getNewSession();

	    long endTime = System.currentTimeMillis() - startTime;
	    logger.info("Time handling the open: " + endTime);
	    return endTime;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object receiveMessage(String template, String[] avps) {
	try {
	    long time = System.currentTimeMillis();
	    Message actual = responder.get();
	    if (actual == null) {
		logger.info("Time handling the receive: "
			+ (System.currentTimeMillis() - time));
		throw new RuntimeException("No message was received...");
	    }
	    logger.info("Input from user: " + template + ", "
		    + Arrays.toString(avps));
	    encoder.request = request;
	    Message expected = encoder.encodeMessage(template, avps);
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

    public Object sendMessage(String template, String[] avps) {
	try {
	    long startTime = System.currentTimeMillis();
	    encoder.session = connection;
	    request = (Request) encoder.encodeMessage(template, avps);
	    responder = connection.send(request);
	    long endTime = System.currentTimeMillis() - startTime;
	    logger.info("Time handling the sendMessage: " + endTime);
	    return endTime;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    public Object closeConnection() {
	try {
	    long startTime = System.currentTimeMillis();
	    connection.release();
	    stack.stop(10, TimeUnit.SECONDS);
	    long endTime = System.currentTimeMillis() - startTime;
	    logger.info("Time handling the close: " + endTime);
	    return endTime;
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
