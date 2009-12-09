package org.robotframework.jdiameter;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
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
	    stack.start();
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
	    logger.info("Received message: " + prettyPrint(actual));
	    return actual;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * @param msg Message to be sent
     */
    @Override
    public void sendMessage(Object msg) {
	this.lastRequest = (Request) msg;
	try {
	    logger.info("Send message: " + prettyPrint(lastRequest));
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

    String prettyPrint(Message actual) throws AvpDataException {
	StringBuffer printed = new StringBuffer();
	printed.append("Msg{appId=").append(actual.getApplicationId());
	printed.append(",command=").append(actual.getCommandCode());
	printed.append(",e2e=").append(actual.getEndToEndIdentifier());
	printed.append(",hbh=").append(actual.getHopByHopIdentifier());
	printed.append(",isError=").append(actual.isError());
	printed.append(",isProxy=").append(actual.isProxiable());
	printed.append(",isReTransmit=").append(actual.isReTransmitted());
	printed.append(",isRequest=").append(actual.isRequest());
	printed.append(",sessionId=").append(actual.getSessionId());
	printed.append("}\n");
	prettyPrint(printed, actual.getApplicationIdAvps());
	prettyPrint(printed, actual.getAvps());
	return printed.toString();
    }

    void prettyPrint(StringBuffer printed, Set<ApplicationId> appIds) {
	printed.append("Application AVPs[");
	for (ApplicationId applicationId : appIds) {
	    printed.append("appId.vendorId=").append(
		    applicationId.getVendorId()).append(",");
	    printed.append("appId.acctId=")
		    .append(applicationId.getAcctAppId()).append(",");
	    printed.append("appId.authId=")
		    .append(applicationId.getAuthAppId()).append("\n");
	}
	printed.append("]\n");
    }

    void prettyPrint(StringBuffer printed, AvpSet avps) 
    	throws AvpDataException {
	if (avps == null) {
	    return;
	}
	printed.append("AVPs[");
	for (Avp avp : avps) {
	    printed.append("avp.code=").append(avp.getCode()).append(",");
	    printed.append("avp.vendorId=").append(avp.getVendorId()).append(
		    ",");
	    printed.append("avp.raw=").append(avp.getRaw()).append("\n");
	}
	printed.append("]");
    }

}
