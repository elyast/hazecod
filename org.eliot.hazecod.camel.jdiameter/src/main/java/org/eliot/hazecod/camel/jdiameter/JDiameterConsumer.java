package org.eliot.hazecod.camel.jdiameter;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.ExchangeHelper;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;

public class JDiameterConsumer extends DefaultConsumer {

    private final class NetworkListener implements NetworkReqListener {
	public Answer processRequest(Request request) {
	    Exchange exchange = endpoint.createExchange();
	    exchange.getIn().setBody(request);
	    try {
		getProcessor().process(exchange);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
            Object body;
            if (ExchangeHelper.isOutCapable(exchange)) {
                body = exchange.getOut().getBody();
            } else {
                body = exchange.getIn().getBody();
            }
            return (Answer)body;
	}
    }

    Stack stack;
    Configuration configuration;
    Network newtwork;
    Endpoint endpoint;

    public JDiameterConsumer(Endpoint endpoint, Processor processor,
	    JDiameterConfiguration configuration) {
	super(endpoint, processor);
	this.endpoint = endpoint;
	this.configuration = configuration;
    }

    protected void doStart() throws Exception {
	super.doStart();
	stack = new StackImpl();
	stack.init(configuration);

	stack.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);

	newtwork = ((PeerTable) stack.unwrap(PeerTable.class))
		.unwrap(Network.class);
	newtwork.addNetworkReqListener(new NetworkListener(),
		new ApplicationId[] { org.jdiameter.api.ApplicationId
			.createByAccAppId(193, 19302) });
    }

    protected void doStop() throws Exception {
	super.doStop();
	stack.stop(10, TimeUnit.SECONDS);
	newtwork.removeNetworkReqListener(org.jdiameter.api.ApplicationId
		.createByAccAppId(193, 19302));
    }

}
