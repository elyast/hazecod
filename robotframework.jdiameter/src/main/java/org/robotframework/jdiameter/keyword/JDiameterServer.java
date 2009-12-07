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

public class JDiameterServer {

    private static final String CONFIGURATION_XML = "server-configuration.xml";
    Stack stack;
    Configuration configuration;
    Network newtwork;

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
			.createByAccAppId(193, 19302) );

    }

    public void stop() throws IllegalDiameterStateException, InternalException {
	if (stack == null) {
	    return;
	}
	stack.stop(10, TimeUnit.SECONDS);
	stack.destroy();
    }

    public static class NetworkListener implements NetworkReqListener {

	@Override
	public Answer processRequest(Request request) {
	    Answer createAnswer = request.createAnswer(ResultCode.SUCCESS);
	    createAnswer.getAvps().addAvp(416, 4);
	    createAnswer.getAvps().addAvp(415, 0L, 0L, true, false,
		    true);
	    return createAnswer;
	}

    }

}
