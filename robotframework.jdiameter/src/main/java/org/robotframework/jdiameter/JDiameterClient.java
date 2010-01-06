package org.robotframework.jdiameter;

import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.robotframework.jdiameter.mapper.AvpPrinter;
import org.robotframework.protocol.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDiameter client
 * 
 * @author Eliot
 * 
 */
public class JDiameterClient implements Client {

    protected static final int TEN = 10;
    private static Logger logger = LoggerFactory
	    .getLogger(JDiameterClient.class);
    private static final String CONFIGURATION_XML = "configuration.xml";

    Session session;
    Stack stack;
    Future<Message> responder;
    Request lastRequest;
    boolean testingConnection;
    AvpPrinter printer;

    /**
     * @param configuration
     *            JDiameter configuration file (if null internal configuration
     *            is taken)
     */
    public void openConnection(String configuration) {
	try {
	    Configuration config = decodeConfiguration(configuration);
	    stack = new org.jdiameter.client.impl.StackImpl();
	    SessionFactory factory = stack.init(config);
	    if (testingConnection) {
		stack.start();
	    } else {
		stack.start(Mode.ANY_PEER, TEN, TimeUnit.SECONDS);
	    }
	    session = factory.getNewSession();
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    /**
     * 
     */
    public void closeConnection() {
	try {
	    session.release();
	    stack.stop(TEN, TimeUnit.SECONDS);
	    stack.destroy();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * @return Gets message from server
     */
    @Override
    public Object receiveMessage() {
	try {
	    Message actual = responder.get();
	    logger.info("Received message: " + printer.prettyPrint(actual));
	    return actual;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * @param msg
     *            Message to be sent
     */
    @Override
    public void sendMessage(Object msg) {
	this.lastRequest = (Request) msg;
	try {
	    logger.info("Send message: " + printer.prettyPrint(lastRequest));
	    responder = session.send((Request) lastRequest);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Loads JDiameter client configuration from file
     * 
     * @param configuration
     *            if <code>null</code> internal configuration is taken otherwise
     *            path to configuration
     * @return Configuration object
     * @throws Exception
     *             Exception from configuration
     */
    public Configuration decodeConfiguration(String configuration)
	    throws Exception {
	if (configuration == null) {
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    InputStream istream = cl.getResourceAsStream(CONFIGURATION_XML);
	    return new XMLConfiguration(istream);
	}
	return new XMLConfiguration(configuration);
    }

    /**
     * @return Diameter message request factory
     */
    @Override
    public Object getSession() {
	return session;
    }

    /**
     * @return Diameter message answer factory
     */
    @Override
    public Object getLastRequest() {
	return lastRequest;
    }

 

    /**
     * @param b
     *            if testing connection is etablished
     */
    public void setTestingConnection(boolean b) {
	testingConnection = b;
    }

    /**
     * @param printer AVP Printer
     */
    public void setPrinter(AvpPrinter printer) {
	this.printer = printer;
    }
}
