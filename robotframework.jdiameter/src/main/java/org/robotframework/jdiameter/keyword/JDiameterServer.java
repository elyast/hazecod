package org.robotframework.jdiameter.keyword;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

/**
 * 
 * JDiameter server
 * @author Eliot
 *
 */
public class JDiameterServer {

    private static final int TEN = 10;
    private static final int ACCT_APP_ID = 19302;
    private static final int VENDOR_ID = 193;
    private static final String CONFIGURATION_XML = "server-configuration.xml";
    Stack stack;
    Configuration configuration;
    Network newtwork;

    /**
     * @throws Exception when server fails
     */
    public void start() throws Exception {
	stack = new StackImpl();
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	InputStream istream = cl.getResourceAsStream(CONFIGURATION_XML);
	configuration = new XMLConfiguration(istream);
	stack.init(configuration);

	stack.start();

	newtwork = ((PeerTable) stack.unwrap(PeerTable.class))
		.unwrap(Network.class);
	newtwork.addNetworkReqListener(new NetworkListener(),
		org.jdiameter.api.ApplicationId
			.createByAccAppId(VENDOR_ID, ACCT_APP_ID));

    }

    /**
     * Stops server
     * @throws IllegalDiameterStateException server fails
     * @throws InternalException server fails
     */
    public void stop() throws IllegalDiameterStateException, InternalException {
	if (stack == null) {
	    return;
	}
	stack.stop(TEN, TimeUnit.SECONDS);
	stack.destroy();
    }

    static class NetworkListener implements NetworkReqListener {

	static final int EVENT_REQUEST = 4;
	private static final int CC_REQUEST_NO = 415;
	static final int CC_REQUEST_TYPE = 416;

	@Override
	public Answer processRequest(Request request) {
	    Answer createAnswer = request.createAnswer(ResultCode.SUCCESS);
	    createAnswer.getAvps().addAvp(CC_REQUEST_TYPE, EVENT_REQUEST);
	    createAnswer.getAvps().addAvp(CC_REQUEST_NO, 0L, 0L, true, false,
		    true);
	    return createAnswer;
	}

    }

}
